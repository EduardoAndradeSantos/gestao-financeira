package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioRequestDTO;
import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ==========================================================
       ===================== SELF-SERVICE =======================
       ========================================================== */

    public UsuarioResponseDTO buscarUsuarioLogado() {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuarioLogado(UsuarioUpdateRequestDTO dto) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setEndereco(dto.endereco());

        return toResponseDTO(repository.save(usuario));
    }

    public void trocarSenhaUsuarioLogado(String senhaAtual, String novaSenha) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ConflitoDeDadosException("Senha atual inválida");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);
    }

    /* ==========================================================
       ===================== MAPEAMENTO =========================
       ========================================================== */

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getEndereco(),
                usuario.getNumeroConta()
        );
    }
}
