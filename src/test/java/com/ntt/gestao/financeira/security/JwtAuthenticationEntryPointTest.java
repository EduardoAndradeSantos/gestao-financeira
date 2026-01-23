package com.ntt.gestao.financeira.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

public class JwtAuthenticationEntryPointTest {

    @Test
    void deveRetornar401EJsonQuandoUsuarioNaoAutenticado() throws Exception {
        JwtAuthenticationEntryPoint entryPoint =
                new JwtAuthenticationEntryPoint();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        BadCredentialsException exception =
                new BadCredentialsException("Credenciais inválidas");

        entryPoint.commence(request, response, exception);

        // status
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        // content-type
        assertEquals(
                "application/json",
                response.getContentType()
        );

        // corpo
        String body = response.getContentAsString();

        assertNotNull(body);
        assertTrue(body.contains("Usuário precisa estar logado para acessar este recurso"));
        assertTrue(body.contains("erro"));
    }
}
