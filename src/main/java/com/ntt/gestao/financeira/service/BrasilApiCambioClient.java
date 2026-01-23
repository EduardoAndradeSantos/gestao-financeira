package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.CambioResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BrasilApiCambioClient {

    private final RestTemplate restTemplate;

    @Value("${brasilapi.url}")
    private String baseUrl;

    public BrasilApiCambioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MoedaResponseDTO[] listarMoedas() {
        return restTemplate.getForObject(
                baseUrl + "/cambio/v1/moedas",
                MoedaResponseDTO[].class
        );
    }

    public CambioResponseDTO buscarCotacao(String moeda, String data) {
        return restTemplate.getForObject(
                baseUrl + "/cambio/v1/cotacao/{moeda}/{data}",
                CambioResponseDTO.class,
                moeda,
                data
        );
    }
}
