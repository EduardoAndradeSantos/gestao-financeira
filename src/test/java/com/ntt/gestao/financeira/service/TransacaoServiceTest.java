package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.TransacaoRequestDTO;
import com.ntt.gestao.financeira.dto.request.TransacaoTransferenciaDTO;
import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransacaoServiceTest {

    private TransacaoRepository repository;
    private UsuarioRepository usuarioRepository;
    private TransacaoService service;
    private MockedStatic<SecurityUtils> securityMock;

    @BeforeEach
    void setup() {
        repository = mock(TransacaoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new TransacaoService(repository, usuarioRepository);

        securityMock = mockStatic(SecurityUtils.class);
        securityMock.when(SecurityUtils::getUsuarioId).thenReturn(1L);

        // 👉 IMPORTANTE: usuário logado SEMPRE existe nos testes
        Usuario usuarioLogado = Usuario.builder().id(1L).build();
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioLogado));
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    /* ===================== DEPOSITO ===================== */

    @Test
    void deveCriarDepositoComSucesso() {
        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoRequestDTO dto = new TransacaoRequestDTO(
                "Depósito",
                new BigDecimal("100"),
                TipoTransacao.DEPOSITO,
                null,
                null
        );

        var response = service.salvar(dto);

        assertEquals(TipoTransacao.DEPOSITO, response.tipo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deveFalharDepositoComCategoria() {
        TransacaoRequestDTO dto = new TransacaoRequestDTO(
                "Erro",
                new BigDecimal("50"),
                TipoTransacao.DEPOSITO,
                CategoriaTransacao.OUTROS,
                null
        );

        assertThrows(ConflitoDeDadosException.class,
                () -> service.salvar(dto));
    }

    /* ===================== TRANSFERENCIA ===================== */

    @Test
    void deveTransferirComSucesso() {
        Usuario destino = Usuario.builder()
                .id(2L)
                .numeroConta("222")
                .build();

        when(usuarioRepository.findByNumeroConta("222"))
                .thenReturn(Optional.of(destino));

        when(repository.calcularSaldoUsuario(1L))
                .thenReturn(new BigDecimal("500"));

        TransacaoTransferenciaDTO dto =
                new TransacaoTransferenciaDTO(
                        "111",
                        "222",
                        new BigDecimal("100"),
                        "PIX"
                );

        service.transferir(dto);

        verify(repository, times(2)).save(any());
    }

    @Test
    void deveFalharTransferenciaComSaldoInsuficiente() {
        Usuario destino = Usuario.builder()
                .id(2L)
                .numeroConta("222")
                .build();

        when(usuarioRepository.findByNumeroConta("222"))
                .thenReturn(Optional.of(destino));

        when(repository.calcularSaldoUsuario(1L))
                .thenReturn(BigDecimal.ZERO);

        TransacaoTransferenciaDTO dto =
                new TransacaoTransferenciaDTO(
                        "111",
                        "222",
                        new BigDecimal("100"),
                        "PIX"
                );

        assertThrows(ConflitoDeDadosException.class,
                () -> service.transferir(dto));
    }
}
