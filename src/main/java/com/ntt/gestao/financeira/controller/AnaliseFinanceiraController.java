package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.dto.response.ResumoFinanceiroDTO;
import com.ntt.gestao.financeira.service.AnaliseFinanceiraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analise")
public class AnaliseFinanceiraController {

    private final AnaliseFinanceiraService service;

    public AnaliseFinanceiraController(AnaliseFinanceiraService service) {
        this.service = service;
    }

    @GetMapping("/{numeroConta}/resumo")
    public ResumoFinanceiroDTO resumo(@PathVariable String numeroConta) {
        return service.resumoPorConta(numeroConta);
    }

    @GetMapping("/{numeroConta}/despesas-por-categoria")
    public List<DespesaPorCategoriaDTO> despesasPorCategoria(
            @PathVariable String numeroConta
    ) {
        return service.despesasPorCategoria(numeroConta);
    }
}
