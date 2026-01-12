package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.TransacaoPorContaRequestDTO;
import com.ntt.gestao.financeira.dto.request.TransacaoRequestDTO;
import com.ntt.gestao.financeira.dto.request.TransacaoTransferenciaDTO;
import com.ntt.gestao.financeira.dto.response.TransacaoResponseDTO;
import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import com.ntt.gestao.financeira.entity.Transacao;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public TransacaoResponseDTO salvarPorConta(TransacaoPorContaRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByNumeroConta(dto.numeroConta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada!"));

        // REGRA DE NEGÓCIO
        CategoriaTransacao categoria = dto.categoria();

        if (dto.tipo() == TipoTransacao.RETIRADA && categoria == null) {
            throw new ConflitoDeDadosException(
                    "Categoria é obrigatória para transações do tipo RETIRADA"
            );
        }

        if (dto.tipo() == TipoTransacao.DEPOSITO && categoria != null) {
            throw new ConflitoDeDadosException(
                    "Categoria não deve ser informada para transações do tipo DEPOSITO"
            );
        }

        if (dto.tipo() == TipoTransacao.TRANSFERENCIA) {
            categoria = CategoriaTransacao.OUTROS;
        }

        Transacao transacao = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(dto.data())
                .tipo(dto.tipo())
                .categoria(categoria)
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

    @Transactional
    public void transferir(TransacaoTransferenciaDTO dto) {

        Usuario origem = usuarioRepository.findByNumeroConta(dto.contaOrigem())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de origem não encontrada"));

        Usuario destino = usuarioRepository.findByNumeroConta(dto.contaDestino())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada"));

        if (origem.getId().equals(destino.getId())) {
            throw new ConflitoDeDadosException("Não é possível transferir para a mesma conta!");
        }

        BigDecimal saldoOrigem = repository.calcularSaldoUsuario(origem.getId());
        if (saldoOrigem.compareTo(dto.valor()) < 0) {
            throw new ConflitoDeDadosException("Saldo insuficiente!");
        }

        // RETIRADA da origem
        Transacao debito = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(LocalDate.now())
                .tipo(TipoTransacao.TRANSFERENCIA)
                .categoria(CategoriaTransacao.OUTROS)
                .usuario(origem)
                .build();

        // DEPÓSITO ao destino
        Transacao credito = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(LocalDate.now())
                .tipo(TipoTransacao.DEPOSITO)
                .categoria(CategoriaTransacao.OUTROS)
                .usuario(destino)
                .build();

        repository.save(debito);
        repository.save(credito);
    }


}
