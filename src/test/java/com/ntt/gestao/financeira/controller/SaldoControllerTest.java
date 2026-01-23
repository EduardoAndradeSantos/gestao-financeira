package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.SaldoResponseDTO;
import com.ntt.gestao.financeira.entity.RoleUsuario;
import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.security.UserPrincipal;
import com.ntt.gestao.financeira.service.SaldoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SaldoController.class)
class SaldoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaldoClient saldoClient;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveConsultarSaldo() throws Exception {

        UserPrincipal principal = new UserPrincipal(
                1L,
                "eduardo@email.com",
                "123456",
                RoleUsuario.USER
        );

        when(saldoClient.buscarSaldo("123456"))
                .thenReturn(new SaldoResponseDTO("123456", BigDecimal.TEN));

        mockMvc.perform(
                        get("/saldo")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                principal,
                                                null,
                                                principal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isOk());
    }
}

