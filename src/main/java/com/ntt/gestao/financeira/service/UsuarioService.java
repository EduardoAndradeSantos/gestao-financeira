package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioRequestDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new ConflitoDeDadosException("CPF já cadastrado!");
        }
        if (repository.existsByEmail(dto.email())) {
            throw new ConflitoDeDadosException("Email já cadastrado!");

        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .endereco(dto.endereco())
                .email(dto.email())
                .senha(dto.senha())
                .numeroConta(gerarNumeroConta())
                .build();

        Usuario salvo = repository.save(usuario);
        return toDTO(salvo);
    }

    public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));
        return toDTO(usuario);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));

        usuario.setNome(dto.nome());
        usuario.setEndereco(dto.endereco());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        return toDTO(repository.save(usuario));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    private String gerarNumeroConta() {
        // Exemplo simples: 8 dígitos aleatórios
        return String.valueOf((int) (Math.random() * 90000000) + 10000000);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEndereco(),
                usuario.getEmail(),
                usuario.getNumeroConta()
        );
    }
}
