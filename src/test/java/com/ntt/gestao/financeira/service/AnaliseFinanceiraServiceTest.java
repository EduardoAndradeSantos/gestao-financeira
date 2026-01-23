package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnaliseFinanceiraServiceTest {

    private TransacaoRepository transacaoRepository;
    private UsuarioRepository usuarioRepository;
    private AnaliseFinanceiraService service;
    private MockedStatic<SecurityUtils> securityMock;

    @BeforeEach
    void setup() {
        transacaoRepository = mock(TransacaoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new AnaliseFinanceiraService(
                transacaoRepository,
                usuarioRepository
        );

        securityMock = mockStatic(SecurityUtils.class);
        securityMock.when(SecurityUtils::getUsuarioId).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    @Test
    void deveRetornarResumoFinanceiro() {
        when(transacaoRepository.calcularSaldoUsuario(1L))
                .thenReturn(new BigDecimal("100"));
        when(transacaoRepository.totalReceitas(1L))
                .thenReturn(new BigDecimal("300"));
        when(transacaoRepository.totalDespesas(1L))
                .thenReturn(new BigDecimal("200"));

        Map<String, BigDecimal> resumo = service.resumoFinanceiro();

        assertEquals(new BigDecimal("100"), resumo.get("saldo"));
        assertEquals(new BigDecimal("300"), resumo.get("receitas"));
        assertEquals(new BigDecimal("200"), resumo.get("despesas"));
    }
}
