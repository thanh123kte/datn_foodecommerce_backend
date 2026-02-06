package com.example.qtifood.services.chatbot;

import com.example.qtifood.constants.ChatbotConstants;
import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.dtos.Chatbot.ChatbotMessageRequest;
import com.example.qtifood.dtos.Chatbot.ChatbotMessageResponse;
import com.example.qtifood.dtos.Chatbot.ToolTraceEntry;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;
import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.MessageType;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.AddressService;
import com.example.qtifood.services.ConversationService;
import com.example.qtifood.services.MessageService;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.ThinkingLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final ToolExecutor toolExecutor;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final Client geminiClient;
    @Qualifier("geminiModelName")
    private final String geminiModelName;

    public ChatbotMessageResponse handleMessage(ChatbotMessageRequest request) {
        String customerId = request.getCustomerId();
        if (!StringUtils.hasText(customerId) || !StringUtils.hasText(request.getText())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId and text are required");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        boolean isCustomerRole = customer.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.CUSTOMER);
        if (!isCustomerRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to chat with bot");
        }

        Long conversationId = resolveConversation(request, customerId);

        // Save customer message
        CreateMessageDto customerMessage = CreateMessageDto.builder()
                .conversationId(conversationId)
                .content(request.getText())
                .messageType(MessageType.TEXT)
                .build();
        messageService.sendMessage(customerId, customerMessage);

        List<MessageResponseDto> historyMessages = messageService.getMessages(conversationId, customerId);
        List<MessageResponseDto> trimmedHistory = trimHistory(historyMessages);
        AddressResponseDto defaultAddress = findDefaultAddress(customerId);

        String userPrompt = buildUserPrompt(request.getText(), customerId, trimmedHistory, defaultAddress);
        GenerateContentConfig config = buildContentConfig();

        List<Content> contents = new ArrayList<>();
        contents.add(Content.fromParts(Part.fromText(userPrompt)));

        List<ToolTraceEntry> toolTrace = new ArrayList<>();
        String replyText = null;
        boolean guardrailRetried = false;

        for (int i = 0; i < 3; i++) {
            GenerateContentResponse response = geminiClient.models.generateContent(geminiModelName, contents, config);

            List<FunctionCall> functionCalls = response.functionCalls();
            if (!CollectionUtils.isEmpty(functionCalls)) {
                for (FunctionCall functionCall : functionCalls) {
                    ToolExecutor.ToolExecutionResult executionResult = toolExecutor.execute(functionCall);
                    toolTrace.add(toTraceEntry(executionResult));

                    contents.add(Content.fromParts(
                            Part.fromFunctionCall(
                                    functionCall.name().orElse(""),
                                    functionCall.args().orElse(Map.of())
                            )));
                    contents.add(Content.fromParts(
                            Part.fromFunctionResponse(
                                    functionCall.name().orElse(""),
                                    Map.of("result", executionResult.payload())
                            )));
                }
                continue;
            }

            replyText = response.text();
            if (toolTrace.isEmpty() && needsToolRetry(replyText) && !guardrailRetried) {
                guardrailRetried = true;
                contents.add(Content.fromParts(Part.fromText("You must call tools for factual details.")));
                continue;
            }
            break;
        }

        if (!StringUtils.hasText(replyText)) {
            replyText = "Xin loi, he thong dang ban. Ban vui long thu lai sau.";
        }

        // Persist bot reply
        CreateMessageDto botMessage = CreateMessageDto.builder()
                .conversationId(conversationId)
                .content(replyText)
                .messageType(MessageType.TEXT)
                .build();
        messageService.sendMessage(ChatbotConstants.BOT_USER_ID, botMessage);

        return ChatbotMessageResponse.builder()
                .conversationId(conversationId)
                .botUserId(ChatbotConstants.BOT_USER_ID)
                .reply(replyText)
                .toolTrace(toolTrace)
                .build();
    }

    private Long resolveConversation(ChatbotMessageRequest request, String customerId) {
        if (request.getConversationId() != null && request.getConversationId() > 0) {
            try {
                ConversationDetailDto detail = conversationService.getConversationDetail(request.getConversationId(), customerId);
                if (detail != null
                        && detail.getCustomer() != null
                        && detail.getSeller() != null
                        && customerId.equals(detail.getCustomer().id())
                        && ChatbotConstants.BOT_USER_ID.equals(detail.getSeller().id())) {
                    return detail.getId();
                }
            } catch (Exception ex) {
                log.warn("Invalid conversation {} for customer {}. Creating new one. Reason: {}", request.getConversationId(), customerId, ex.getMessage());
            }
        }

        ConversationResponseDto conversation = conversationService.getOrCreateConversation(customerId, ChatbotConstants.BOT_USER_ID);
        return conversation.getId();
    }

    private List<MessageResponseDto> trimHistory(List<MessageResponseDto> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return List.of();
        }
        int size = messages.size();
        if (size <= ChatbotConstants.HISTORY_LIMIT) {
            return messages;
        }
        return messages.subList(size - ChatbotConstants.HISTORY_LIMIT, size);
    }

    private AddressResponseDto findDefaultAddress(String customerId) {
        List<AddressResponseDto> addresses = Optional.ofNullable(addressService.getAddressesByUserId(customerId)).orElse(List.of());
        return addresses.stream()
                .filter(AddressResponseDto::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    private String buildUserPrompt(String text, String customerId, List<MessageResponseDto> history, AddressResponseDto defaultAddress) {
        String historySection = CollectionUtils.isEmpty(history)
                ? "None"
                : history.stream()
                .map(msg -> {
                    String senderRole = ChatbotConstants.BOT_USER_ID.equals(msg.getSender().id()) ? "Bot" : "Customer";
                    return senderRole + ": " + msg.getContent();
                })
                .collect(Collectors.joining("\n"));

        String defaultAddr = defaultAddress != null ? defaultAddress.getAddress() : "null";

        return """
                Customer message: "%s"

                Conversation history (up to 20, labeled):
                %s

                Known customer context:
                - customerId: %s
                - defaultAddress (if available): %s

                Instruction:
                Answer in Vietnamese. Use tools for factual details. Recommend up to 5 items. Ask one follow-up question.
                """.formatted(text, historySection, customerId, defaultAddr);
    }

    private GenerateContentConfig buildContentConfig() {
        GenerateContentConfig.Builder builder = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(ChatbotConstants.SYSTEM_INSTRUCTION)))
                .tools(toolExecutor.getTools());

        // Some models (e.g., gemini-2.5-flash) do not support thinking config
        if (supportsThinkingConfig(geminiModelName)) {
            builder.thinkingConfig(ThinkingConfig.builder()
                    .thinkingLevel(new ThinkingLevel("high"))
                    .build());
        }

        return builder.build();
    }

    private ToolTraceEntry toTraceEntry(ToolExecutor.ToolExecutionResult executionResult) {
        return ToolTraceEntry.builder()
                .tool(executionResult.toolName())
                .args(executionResult.args())
                .count(executionResult.resultCount())
                .build();
    }

    private boolean needsToolRetry(String reply) {
        if (!StringUtils.hasText(reply)) {
            return false;
        }
        String lower = reply.toLowerCase();
        return lower.matches(".*\\d+\\s?(\\u0111|\\u20ab|vnd|vn\\u0111|k).*")
                || lower.contains("dia chi")
                || lower.contains("address")
                || lower.contains("gio")
                || lower.contains("opening")
                || lower.contains("closing");
    }

    private boolean supportsThinkingConfig(String modelName) {
        if (!StringUtils.hasText(modelName)) {
            return false;
        }
        String normalized = modelName.toLowerCase();
        return !(normalized.contains("2.5-flash"));
    }
}
