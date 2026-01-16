package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.LoginRequestDTO;
import com.ntt.gestao.financeira.dto.response.LoginResponseDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo processo de autenticação.
 *
 * Realiza:
 * - Validação das credenciais do usuário
 * - Geração do token JWT
 *
 * Este é o único endpoint público relacionado à segurança.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Repositório para consulta de usuários por e-mail
    private final UsuarioRepository usuarioRepository;

    // Encoder utilizado para validar a senha informada
    private final PasswordEncoder passwordEncoder;

    // Serviço responsável por gerar o token JWT
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Realiza o login do usuário.
     *
     * Fluxo:
     * 1. Busca o usuário pelo e-mail
     * 2. Valida a senha utilizando BCrypt
     * 3. Gera um token JWT em caso de sucesso
     *
     * Em caso de erro, retorna credenciais inválidas.
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

        // Busca o usuário pelo e-mail informado
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        // Valida a senha informada com a senha criptografada
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Gera o token JWT para o usuário autenticado
        String token = jwtService.gerarToken(usuario);

        // Retorna o token e o tipo (Bearer)
        return new LoginResponseDTO(token, "Bearer");
    }
}
