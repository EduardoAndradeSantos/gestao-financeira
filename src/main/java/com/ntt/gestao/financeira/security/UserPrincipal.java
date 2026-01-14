package com.ntt.gestao.financeira.security;

import com.ntt.gestao.financeira.entity.RoleUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long usuarioId;
    private final String email;
    private final String numeroConta;
    private final RoleUsuario role;

    public UserPrincipal(
            Long usuarioId,
            String email,
            String numeroConta,
            RoleUsuario role
    ) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.numeroConta = numeroConta;
        this.role = role;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public RoleUsuario getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
