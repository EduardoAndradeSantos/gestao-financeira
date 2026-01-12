package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.CotacaoResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaCambioDTO;
import com.ntt.gestao.financeira.service.CambioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cambio")
public class CambioController {

    private final CambioService service;

    public CambioController(CambioService service) {
        this.service = service;
    }

    @GetMapping("/moedas")
    public List<MoedaCambioDTO> listarMoedas() {
        return service.listarMoedas();
    }

    @GetMapping("/cotacao/{moeda}/{data}")
    public CotacaoResponseDTO buscarCotacao(
            @PathVariable String moeda,
            @PathVariable String data
    ) {
        return service.buscarCotacao(moeda, data);
    }
}
