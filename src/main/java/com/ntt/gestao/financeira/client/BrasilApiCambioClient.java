package com.ntt.gestao.financeira.client;

import com.ntt.gestao.financeira.dto.response.CotacaoResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaCambioDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BrasilApiCambioClient {

    private static final String BASE_URL = "https://brasilapi.com.br/api/cambio/v1";
    private final RestTemplate restTemplate = new RestTemplate();

    public MoedaCambioDTO[] listarMoedas() {
        return restTemplate.getForObject(
                BASE_URL + "/moedas",
                MoedaCambioDTO[].class
        );
    }

    public CotacaoResponseDTO buscarCotacao(String moeda, String data) {
        return restTemplate.getForObject(
                BASE_URL + "/cotacao/{moeda}/{data}",
                CotacaoResponseDTO.class,
                moeda,
                data
        );
    }
}

