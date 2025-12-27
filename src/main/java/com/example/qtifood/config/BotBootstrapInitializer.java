package com.example.qtifood.config;

import com.example.qtifood.constants.ChatbotConstants;
import com.example.qtifood.entities.Role;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.repositories.RoleRepository;
import com.example.qtifood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotBootstrapInitializer implements ApplicationRunner {

    private static final String BOT_PASSWORD_PLACEHOLDER = "QTI_BOT_PLACEHOLDER_PASSWORD";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role sellerRole = resolveSellerRole();

        User botUser = userRepository.findById(ChatbotConstants.BOT_USER_ID).orElseGet(() -> {
            User user = User.builder()
                    .id(ChatbotConstants.BOT_USER_ID)
                    .fullName(ChatbotConstants.BOT_FULL_NAME)
                    .password(passwordEncoder.encode(BOT_PASSWORD_PLACEHOLDER))
                    .isActive(true)
                    .roles(new HashSet<>())
                    .build();
            user.getRoles().add(sellerRole);
            try {
                return userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                // Another instance inserted concurrently; fetch existing
                return userRepository.findById(ChatbotConstants.BOT_USER_ID)
                        .orElseThrow(() -> e);
            }
        });

        boolean updated = false;
        if (!ChatbotConstants.BOT_FULL_NAME.equals(botUser.getFullName())) {
            botUser.setFullName(ChatbotConstants.BOT_FULL_NAME);
            updated = true;
        }

        if (botUser.getRoles().stream().noneMatch(role -> role.getName() == RoleType.SELLER)) {
            botUser.getRoles().add(sellerRole);
            updated = true;
        }

        if (updated) {
            userRepository.save(botUser);
        }

        log.info("Chatbot bot user READY");
    }

    private Role resolveSellerRole() {
        return roleRepository.findById(3L)
                .or(() -> roleRepository.findByName(RoleType.SELLER))
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .id(3L)
                                .name(RoleType.SELLER)
                                .description("Seller role for chatbot")
                                .build()
                ));
    }
}
