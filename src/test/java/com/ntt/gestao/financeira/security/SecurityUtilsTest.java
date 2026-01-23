package com.ntt.gestao.financeira.security;

import com.ntt.gestao.financeira.entity.RoleUsuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilsTest {

    @BeforeEach
    void setup() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "edu@email.com",
                "12345678",
                RoleUsuario.USER
        );

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornarPrincipalDoContexto() {
        UserPrincipal result = SecurityUtils.getPrincipal();

        assertNotNull(result);
        assertEquals(1L, result.getUsuarioId());
    }

    @Test
    void deveRetornarUsuarioIdDoPrincipal() {
        Long usuarioId = SecurityUtils.getUsuarioId();

        assertEquals(1L, usuarioId);
    }

    @Test
    void deveRetornarNumeroContaDoPrincipal() {
        String numeroConta = SecurityUtils.getNumeroConta();

        assertEquals("12345678", numeroConta);
    }

    @Test
    void deveRetornarEmailDoPrincipal() {
        String email = SecurityUtils.getEmail();

        assertEquals("edu@email.com", email);
    }
}
