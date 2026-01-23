package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.service.AnaliseFinanceiraService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelas análises financeiras do usuário.
 *
 * Expõe endpoints para:
 * - Resumo financeiro (entradas x saídas)
 * - Distribuição de despesas por categoria
 *
 * Todas as análises são realizadas
 * com base nas transações do usuário autenticado.
 */
@RestController
@RequestMapping("/analise")
public class AnaliseFinanceiraController {

    // Service que concentra as regras de cálculo e agregação financeira
    private final AnaliseFinanceiraService service;

    public AnaliseFinanceiraController(AnaliseFinanceiraService service) {
        this.service = service;
    }

    /**
     * Retorna um resumo financeiro do usuário.
     *
     * O resumo normalmente contém:
     * - Total de entradas
     * - Total de saídas
     * - Saldo consolidado
     *
     * Os valores são retornados em um Map
     * para facilitar consumo no frontend.
     */
    @GetMapping("/resumo")
    public Map<String, BigDecimal> resumo() {
        return service.resumoFinanceiro();
    }

    /**
     * Retorna a lista de despesas agrupadas por categoria.
     *
     * Utilizado para gráficos e análises de comportamento financeiro,
     * como pizza ou barras no frontend.
     */
    @GetMapping("/despesas-por-categoria")
    public List<DespesaPorCategoriaDTO> despesasPorCategoria() {
        return service.despesasPorCategoria();
    }
}
