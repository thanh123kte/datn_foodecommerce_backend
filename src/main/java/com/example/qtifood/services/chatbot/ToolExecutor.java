package com.example.qtifood.services.chatbot;

import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.dtos.Products.ProductResponseDto;
import com.example.qtifood.dtos.Stores.StoreResponseDto;
import com.example.qtifood.exceptions.BadRequestException;
import com.example.qtifood.services.AddressService;
import com.example.qtifood.services.ProductService;
import com.example.qtifood.services.StoreService;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.Tool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private static final int MAX_RESULTS = 10;

    private final ToolRegistry toolRegistry;
    private final StoreService storeService;
    private final ProductService productService;
    private final AddressService addressService;

    private final Gson gson = new GsonBuilder().create();

    public List<Tool> getTools() {
        return toolRegistry.getTools();
    }

    public ToolExecutionResult execute(FunctionCall functionCall) {
        String name = functionCall.name().orElse("unknown");
        Map<String, Object> args = functionCall.args().orElse(Map.of());

        try {
            return switch (name) {
                case "searchStores" -> handleSearchStores(args);
                case "getStoreById" -> handleGetStore(args);
                case "searchProducts" -> handleSearchProducts(args);
                case "getProductById" -> handleGetProduct(args);
                case "getProductsByStore" -> handleGetProductsByStore(args);
                case "getProductsByCategory" -> handleGetProductsByCategory(args);
                case "getCustomerAddresses" -> handleGetAddresses(args);
                default -> errorResult(name, args, "Unsupported tool: " + name);
            };
        } catch (BadRequestException ex) {
            // Return a structured error payload to the model instead of failing the conversation
            return errorResult(name, args, ex.getMessage());
        }
    }

    private ToolExecutionResult handleSearchStores(Map<String, Object> args) {
        String query = stringArg(args, "query");
        List<StoreResponseDto> stores = Optional.ofNullable(storeService.searchByName(query)).orElse(List.of());
        List<Map<String, Object>> items = stores.stream()
                .limit(MAX_RESULTS)
                .map(this::toStoreMap)
                .toList();
        return buildResult("searchStores", Map.of("query", query), items);
    }

    private ToolExecutionResult handleGetStore(Map<String, Object> args) {
        Long storeId = longArg(args, "storeId");
        StoreResponseDto store = storeService.getStoreById(storeId);
        List<Map<String, Object>> items = store != null ? List.of(toStoreMap(store)) : List.of();
        return buildResult("getStoreById", Map.of("storeId", storeId), items);
    }

    private ToolExecutionResult handleSearchProducts(Map<String, Object> args) {
        String query = stringArg(args, "query");
        List<ProductResponseDto> products = Optional.ofNullable(productService.searchProductsByName(query)).orElse(List.of());
        List<Map<String, Object>> items = products.stream()
                .limit(MAX_RESULTS)
                .map(this::toProductMap)
                .toList();
        return buildResult("searchProducts", Map.of("query", query), items);
    }

    private ToolExecutionResult handleGetProduct(Map<String, Object> args) {
        Long productId = longArg(args, "productId");
        ProductResponseDto product = productService.getProductById(productId);
        List<Map<String, Object>> items = product != null ? List.of(toProductMap(product)) : List.of();
        return buildResult("getProductById", Map.of("productId", productId), items);
    }

    private ToolExecutionResult handleGetProductsByStore(Map<String, Object> args) {
        Long storeId = longArg(args, "storeId");
        List<ProductResponseDto> products = Optional.ofNullable(productService.getProductsByStore(storeId)).orElse(List.of());
        List<Map<String, Object>> items = products.stream()
                .limit(MAX_RESULTS)
                .map(this::toProductMap)
                .toList();
        return buildResult("getProductsByStore", Map.of("storeId", storeId), items);
    }

    private ToolExecutionResult handleGetProductsByCategory(Map<String, Object> args) {
        Long categoryId = longArg(args, "categoryId");
        List<ProductResponseDto> products = Optional.ofNullable(productService.getProductsByCategory(categoryId)).orElse(List.of());
        List<Map<String, Object>> items = products.stream()
                .limit(MAX_RESULTS)
                .map(this::toProductMap)
                .toList();
        return buildResult("getProductsByCategory", Map.of("categoryId", categoryId), items);
    }

    private ToolExecutionResult handleGetAddresses(Map<String, Object> args) {
        String customerId = stringArg(args, "customerId");
        List<AddressResponseDto> addresses = Optional.ofNullable(addressService.getAddressesByUserId(customerId)).orElse(List.of());
        List<Map<String, Object>> items = addresses.stream()
                .limit(MAX_RESULTS)
                .map(this::toAddressMap)
                .toList();
        return buildResult("getCustomerAddresses", Map.of("customerId", customerId), items);
    }

    private ToolExecutionResult buildResult(String toolName, Map<String, Object> normalizedArgs, List<Map<String, Object>> items) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", items);
        payload.put("count", CollectionUtils.isEmpty(items) ? 0 : items.size());
        return new ToolExecutionResult(toolName, normalizedArgs, payload, gson.toJson(payload), items.size());
    }

    private ToolExecutionResult errorResult(String toolName, Map<String, Object> normalizedArgs, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", List.of());
        payload.put("count", 0);
        payload.put("error", message);
        return new ToolExecutionResult(toolName, normalizedArgs, payload, gson.toJson(payload), 0);
    }

    private Map<String, Object> toStoreMap(StoreResponseDto store) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", store.getId());
        map.put("name", store.getName());
        map.put("address", store.getAddress());
        map.put("openTime", store.getOpenTime());
        map.put("closeTime", store.getCloseTime());
        map.put("status", store.getStatus() != null ? store.getStatus().name() : null);
        map.put("latitude", store.getLatitude());
        map.put("longitude", store.getLongitude());
        return map;
    }

    private Map<String, Object> toProductMap(ProductResponseDto product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("price", product.getPrice());
        map.put("discountPrice", product.getDiscountPrice());
        map.put("status", product.getStatus() != null ? product.getStatus().name() : null);
        map.put("storeId", product.getStoreId());
        map.put("storeName", product.getStoreName());
        map.put("categoryId", product.getCategoryId());
        return map;
    }

    private Map<String, Object> toAddressMap(AddressResponseDto address) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", address.getId());
        map.put("address", address.getAddress());
        map.put("isDefault", address.getIsDefault());
        map.put("city", null);
        map.put("district", null);
        map.put("latitude", address.getLatitude());
        map.put("longitude", address.getLongitude());
        return map;
    }

    private String stringArg(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null || !StringUtils.hasText(value.toString())) {
            throw new BadRequestException("Missing required argument: " + key);
        }
        return value.toString();
    }

    private Long longArg(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String str && StringUtils.hasText(str)) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Invalid numeric argument for " + key);
            }
        }
        throw new BadRequestException("Missing required argument: " + key);
    }

    public record ToolExecutionResult(String toolName,
                                      Map<String, Object> args,
                                      Object payload,
                                      String jsonPayload,
                                      int resultCount) {
    }
}
