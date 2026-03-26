package com.ciao.clinica.backend.security.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.ciao.clinica.backend.security.CustomUserDetails;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getId();
        }

        return null;
    }
}