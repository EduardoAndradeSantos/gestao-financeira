package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.CambioResponseDTO;
import com.ntt.gestao.financeira.dto.response.MoedaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BrasilApiCambioClienteTest {

    private RestTemplate restTemplate;
    private BrasilApiCambioClient client;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        client = new BrasilApiCambioClient(restTemplate);

        ReflectionTestUtils.setField(
                client,
                "baseUrl",
                "https://brasilapi.com.br/api"
        );
    }

    @Test
    void deveListarMoedasComSucesso() {
        MoedaResponseDTO[] moedas = {
                new MoedaResponseDTO("USD", "Dólar", "FIAT"),
                new MoedaResponseDTO("EUR", "Euro", "FIAT")
        };

        when(restTemplate.getForObject(
                "https://brasilapi.com.br/api/cambio/v1/moedas",
                MoedaResponseDTO[].class
        )).thenReturn(moedas);

        MoedaResponseDTO[] response = client.listarMoedas();

        assertNotNull(response);
        assertEquals(2, response.length);
        assertEquals("USD", response[0].simbolo());

        verify(restTemplate).getForObject(
                "https://brasilapi.com.br/api/cambio/v1/moedas",
                MoedaResponseDTO[].class
        );
    }

    @Test
    void deveRetornarNullQuandoApiNaoResponderEmListarMoedas() {
        when(restTemplate.getForObject(
                anyString(),
                eq(MoedaResponseDTO[].class)
        )).thenReturn(null);

        MoedaResponseDTO[] response = client.listarMoedas();

        assertNull(response);
    }

    @Test
    void deveBuscarCotacaoComSucesso() {
        CambioResponseDTO cambio = new CambioResponseDTO(
                "USD",
                "2024-01-01",
                null
        );

        when(restTemplate.getForObject(
                "https://brasilapi.com.br/api/cambio/v1/cotacao/{moeda}/{data}",
                CambioResponseDTO.class,
                "USD",
                "2024-01-01"
        )).thenReturn(cambio);

        CambioResponseDTO response =
                client.buscarCotacao("USD", "2024-01-01");

        assertNotNull(response);
        assertEquals("USD", response.moeda());

        verify(restTemplate).getForObject(
                "https://brasilapi.com.br/api/cambio/v1/cotacao/{moeda}/{data}",
                CambioResponseDTO.class,
                "USD",
                "2024-01-01"
        );
    }

    @Test
    void deveRetornarNullQuandoApiNaoResponderEmBuscarCotacao() {
        when(restTemplate.getForObject(
                anyString(),
                eq(CambioResponseDTO.class),
                any(),
                any()
        )).thenReturn(null);

        CambioResponseDTO response =
                client.buscarCotacao("USD", "2024-01-01");

        assertNull(response);
    }
}
