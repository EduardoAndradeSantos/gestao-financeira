package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.TransacaoRequestDTO;
import com.ntt.gestao.financeira.dto.response.TransacaoResponseDTO;
import com.ntt.gestao.financeira.entity.Transacao;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public TransacaoResponseDTO salvar(TransacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        Transacao transacao = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(dto.data())
                .tipo(dto.tipo())
                .categoria(dto.categoria())
                .usuario(usuario)
                .build();

        return toDTO(repository.save(transacao));
    }

    public List<TransacaoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public TransacaoResponseDTO buscar(Long id) {
        Transacao transacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada!"));
        return toDTO(transacao);
    }

    public TransacaoResponseDTO atualizar(Long id, TransacaoRequestDTO dto) {
        Transacao transacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada!"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setData(dto.data());
        transacao.setTipo(dto.tipo());
        transacao.setCategoria(dto.categoria());
        transacao.setUsuario(usuario);

        return toDTO(repository.save(transacao));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    private TransacaoResponseDTO toDTO(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getData(),
                transacao.getTipo(),
                transacao.getCategoria(),
                transacao.getUsuario().getId()
        );
    }
}
