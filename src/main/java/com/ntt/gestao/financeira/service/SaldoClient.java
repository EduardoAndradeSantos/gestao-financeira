package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.SaldoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SaldoClient {

    private final RestTemplate restTemplate;

    @Value("${saldo.api.url}")
    private String saldoApiUrl;

    public SaldoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SaldoResponseDTO buscarSaldo(String numeroConta) {
        String url = saldoApiUrl + "/saldo/" + numeroConta;
        return restTemplate.getForObject(url, SaldoResponseDTO.class);
    }
}
