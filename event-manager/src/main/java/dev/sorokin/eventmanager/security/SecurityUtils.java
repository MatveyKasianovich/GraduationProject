package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.entity.UserEntity;

import dev.sorokin.eventmanager.entity.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        SecurityUtils.userRepository = userRepository;
    }

    public static Long getCurrentUserId() {
        String login = getCurrentUserLogin();
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();

    }

    public static String getCurrentUserLogin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();

    }

    public static String getCurrentUserRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().iterator().next().getAuthority();

    }
}
