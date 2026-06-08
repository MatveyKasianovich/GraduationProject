package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public static String getCurrentUserLogin() {
        return getCurrentUser().getLogin();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}