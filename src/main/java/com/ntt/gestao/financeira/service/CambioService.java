package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.client.BrasilApiCambioClient;
import com.ntt.gestao.financeira.dto.response.CotacaoResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaCambioDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CambioService {

    private final BrasilApiCambioClient client;

    public CambioService(BrasilApiCambioClient client) {
        this.client = client;
    }

    public List<MoedaCambioDTO> listarMoedas() {
        return Arrays.asList(client.listarMoedas());
    }

    public CotacaoResponseDTO buscarCotacao(String moeda, String data) {
        return client.buscarCotacao(moeda, data);
    }
}
