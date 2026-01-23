package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.MoedaResponseDTO;
import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.service.BrasilApiCambioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambioController.class)
class CambioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrasilApiCambioClient client;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void deveListarMoedas() throws Exception {

        when(client.listarMoedas())
                .thenReturn(new MoedaResponseDTO[0]);

        mockMvc.perform(get("/cambio/moedas"))
                .andExpect(status().isOk());
    }
}
