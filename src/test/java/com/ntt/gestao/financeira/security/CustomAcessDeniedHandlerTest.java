package com.ntt.gestao.financeira.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomAcessDeniedHandlerTest {

    @Test
    void deveRetornar403EJsonQuandoAcessoNegado() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AccessDeniedException exception =
                new AccessDeniedException("Acesso negado");

        handler.handle(request, response, exception);

        // status
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());

        // content-type
        assertEquals(
                "application/json",
                response.getContentType()
        );

        // corpo
        String body = response.getContentAsString();

        assertNotNull(body);
        assertTrue(body.contains("Você não tem permissão para acessar este recurso"));
        assertTrue(body.contains("erro"));
    }
}
