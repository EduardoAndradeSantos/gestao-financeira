package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.service.RelatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelatorioController.class)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RelatorioService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void deveGerarPdf() throws Exception {

        when(service.gerarPdfUsuarioLogado())
                .thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/relatorios/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio.pdf"
                ));
    }
}
