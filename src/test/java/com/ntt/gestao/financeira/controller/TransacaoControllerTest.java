package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.TransacaoResponseDTO;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void deveCriarTransacao() throws Exception {

        when(service.salvar(any()))
                .thenReturn(new TransacaoResponseDTO(
                        1L,
                        "Teste",
                        BigDecimal.TEN,
                        LocalDateTime.now(),
                        TipoTransacao.DEPOSITO,
                        null,
                        1L
                ));

        mockMvc.perform(post("/transacoes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
              "descricao": "Teste",
              "valor": 10,
              "tipo": "DEPOSITO",
              "usuarioId": 1
            }
        """))
                .andExpect(status().isCreated());
    }
}
