package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.client.BrasilApiCambioClient;
import com.ntt.gestao.financeira.dto.response.MoedaCambioDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CambioServiceTest {

    @Mock
    private BrasilApiCambioClient client;

    @InjectMocks
    private CambioService service;

    @Test
    void deveListarMoedas() {
        MoedaCambioDTO moeda = new MoedaCambioDTO("USD", "Dólar", "A");
        Mockito.when(client.listarMoedas())
                .thenReturn(new MoedaCambioDTO[]{moeda});

        List<MoedaCambioDTO> resultado = service.listarMoedas();

        assertEquals(1, resultado.size());
        assertEquals("USD", resultado.get(0).simbolo());
    }
}

