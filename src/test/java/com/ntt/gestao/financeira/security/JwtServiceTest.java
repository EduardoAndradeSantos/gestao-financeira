package com.ntt.gestao.financeira.security;

import com.ntt.gestao.financeira.entity.RoleUsuario;
import com.ntt.gestao.financeira.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();

        usuario = Usuario.builder()
                .id(1L)
                .email("edu@email.com")
                .numeroConta("12345678")
                .role(RoleUsuario.USER)
                .build();
    }

    @Test
    void deveGerarTokenComSucesso() {
        String token = jwtService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void deveRetornarTrueParaTokenValido() {
        String token = jwtService.gerarToken(usuario);

        boolean valido = jwtService.isTokenValido(token);

        assertTrue(valido);
    }

    @Test
    void deveRetornarFalseParaTokenInvalido() {
        boolean valido = jwtService.isTokenValido("token.invalido.aqui");

        assertFalse(valido);
    }

    @Test
    void deveExtrairUsuarioIdDoToken() {
        String token = jwtService.gerarToken(usuario);

        Long usuarioId = jwtService.getUsuarioId(token);

        assertEquals(1L, usuarioId);
    }

    @Test
    void deveExtrairEmailDoToken() {
        String token = jwtService.gerarToken(usuario);

        String email = jwtService.getEmail(token);

        assertEquals("edu@email.com", email);
    }

    @Test
    void deveExtrairNumeroContaDoToken() {
        String token = jwtService.gerarToken(usuario);

        String numeroConta = jwtService.getNumeroConta(token);

        assertEquals("12345678", numeroConta);
    }

    @Test
    void deveExtrairRoleDoToken() {
        String token = jwtService.gerarToken(usuario);

        String role = jwtService.getRole(token);

        assertEquals("USER", role);
    }
}
