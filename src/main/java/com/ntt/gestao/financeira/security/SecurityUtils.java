package com.ntt.gestao.financeira.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrincipal) auth.getPrincipal();
    }

    public static Long getUsuarioId() {
        return getPrincipal().getUsuarioId();
    }

    public static String getNumeroConta() {
        return getPrincipal().getNumeroConta();
    }

    public static String getEmail() {
        return getPrincipal().getUsername();
    }
}
