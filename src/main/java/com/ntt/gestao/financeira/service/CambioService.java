package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.client.BrasilApiCambioClient;
import com.ntt.gestao.financeira.dto.response.CotacaoResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaCambioDTO;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

        // Converte String → LocalDate
        LocalDate dataInformada = LocalDate.parse(data);

        // Ajusta a data conforme regras da BrasilAPI
        LocalDate dataConsulta = ajustarDataParaConsulta(dataInformada);

        // Chamada final usando a data ajustada
        return client.buscarCotacao(moeda, dataConsulta.toString());
    }

    /**
     * BrasilAPI:
     * - Não permite consultar o dia atual
     * - Não retorna dados para finais de semana
     */
    private LocalDate ajustarDataParaConsulta(LocalDate data) {

        LocalDate hoje = LocalDate.now();

        // Se for hoje ou futuro, volta para ontem
        if (!data.isBefore(hoje)) {
            data = hoje.minusDays(1);
        }

        // Se cair em fim de semana, retrocede até dia útil
        while (data.getDayOfWeek() == DayOfWeek.SATURDAY ||
                data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.minusDays(1);
        }

        return data;
    }
}
