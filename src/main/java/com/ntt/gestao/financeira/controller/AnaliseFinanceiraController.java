package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.service.AnaliseFinanceiraService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analise")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService service;

    public AnaliseFinanceiraController(AnaliseFinanceiraService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public Map<String, BigDecimal> resumo() {
        return service.resumoFinanceiro();
    }

    @GetMapping("/despesas-por-categoria")
    public List<DespesaPorCategoriaDTO> despesasPorCategoria() {
        return service.despesasPorCategoria();
    }
}
