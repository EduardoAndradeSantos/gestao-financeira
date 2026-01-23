package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.service.AnaliseFinanceiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnaliseFinanceiraController.class)
class AnaliseFinanceiraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnaliseFinanceiraService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void deveRetornarResumoFinanceiro() throws Exception {

        when(service.resumoFinanceiro())
                .thenReturn(Map.of(
                        "entradas", BigDecimal.TEN,
                        "saidas", BigDecimal.ONE
                ));

        mockMvc.perform(get("/analise/resumo"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveRetornarDespesasPorCategoria() throws Exception {

        when(service.despesasPorCategoria())
                .thenReturn(List.of(
                        new DespesaPorCategoriaDTO(
                                CategoriaTransacao.ALIMENTACAO,
                                BigDecimal.TEN
                        )
                ));

        mockMvc.perform(get("/analise/despesas-por-categoria"))
                .andExpect(status().isOk());
    }
}
