package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.CambioResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaResponseDTO;
import com.ntt.gestao.financeira.service.BrasilApiCambioClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cambio")
public class CambioController {

    private final BrasilApiCambioClient client;

    public CambioController(BrasilApiCambioClient client) {
        this.client = client;
    }

    @GetMapping("/moedas")
    public MoedaResponseDTO[] listarMoedas() {
        return client.listarMoedas();
    }

    @GetMapping("/{moeda}/{data}")
    public CambioResponseDTO buscarCotacao(
            @PathVariable String moeda,
            @PathVariable String data
    ) {
        return client.buscarCotacao(moeda, data);
    }
}
