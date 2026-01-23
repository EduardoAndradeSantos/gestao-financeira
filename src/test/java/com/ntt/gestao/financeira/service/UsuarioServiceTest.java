package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;
    private UsuarioService service;
    private MockedStatic<SecurityUtils> securityMock;

    @BeforeEach
    void setup() {
        repository = mock(UsuarioRepository.class);
        encoder = mock(PasswordEncoder.class);
        service = new UsuarioService(repository, encoder);

        securityMock = mockStatic(SecurityUtils.class);
        securityMock.when(SecurityUtils::getUsuarioId).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    @Test
    void deveAtualizarUsuarioLogado() {
        Usuario usuario = Usuario.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenReturn(usuario);

        var dto = new UsuarioUpdateRequestDTO("Novo Nome", "Novo Endereço");

        var response = service.atualizarUsuarioLogado(dto);

        assertEquals("Novo Nome", response.nome());
    }

    @Test
    void deveFalharSenhaIncorreta() {
        Usuario usuario = Usuario.builder().id(1L).senha("hash").build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(encoder.matches(any(), any())).thenReturn(false);

        assertThrows(ConflitoDeDadosException.class,
                () -> service.trocarSenhaUsuarioLogado("errada", "nova"));
    }
}
