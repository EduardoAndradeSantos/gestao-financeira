package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.SaldoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaldoClientTest {

    private RestTemplate restTemplate;
    private SaldoClient client;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        client = new SaldoClient(restTemplate);

        ReflectionTestUtils.setField(
                client,
                "saldoApiUrl",
                "http://mock-api"
        );
    }

    @Test
    void deveBuscarSaldo() {
        SaldoResponseDTO mockResponse =
                new SaldoResponseDTO("123", new BigDecimal("250"));

        when(restTemplate.getForObject(
                "http://mock-api/saldo/123",
                SaldoResponseDTO.class
        )).thenReturn(mockResponse);

        SaldoResponseDTO result = client.buscarSaldo("123");

        assertEquals("123", result.numeroConta());
        assertEquals(0, result.saldo().compareTo(new BigDecimal("250")));
    }
}
