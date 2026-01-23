package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.CambioResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaResponseDTO;
import com.ntt.gestao.financeira.service.BrasilApiCambioClient;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela consulta de câmbio.
 *
 * Realiza integração com a Brasil API
 * para obter informações de moedas e cotações.
 *
 * Este módulo é isolado das regras financeiras internas
 * e atua apenas como consulta externa.
 */
@RestController
@RequestMapping("/cambio")
public class CambioController {

    // Cliente HTTP responsável pela comunicação com a Brasil API
    private final BrasilApiCambioClient client;

    public CambioController(BrasilApiCambioClient client) {
        this.client = client;
    }

    /**
     * Lista todas as moedas disponíveis para consulta.
     * Os dados são obtidos diretamente da Brasil API.
     */
    @GetMapping("/moedas")
    public MoedaResponseDTO[] listarMoedas() {
        return client.listarMoedas();
    }

    /**
     * Busca a cotação de uma moeda em uma data específica.
     *
     * @param moeda Código da moeda (ex: USD, EUR)
     * @param data  Data da cotação no formato esperado pela API externa
     */
    @GetMapping("/{moeda}/{data}")
    public CambioResponseDTO buscarCotacao(
            @PathVariable String moeda,
            @PathVariable String data
    ) {
        return client.buscarCotacao(moeda, data);
    }
}
