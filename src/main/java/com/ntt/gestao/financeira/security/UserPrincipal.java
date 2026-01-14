package com.ntt.gestao.financeira.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long usuarioId;
    private final String email;
    private final String numeroConta;

    public UserPrincipal(Long usuarioId, String email, String numeroConta) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.numeroConta = numeroConta;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
