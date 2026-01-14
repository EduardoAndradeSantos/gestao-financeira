package com.ntt.gestao.financeira.service;

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
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(
            TransacaoRepository repository,
            UsuarioRepository usuarioRepository
    ) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    /* ==========================================================
       ====================== UTIL ==============================
       ========================================================== */

    private Usuario getUsuarioLogado() {
        Long usuarioId = SecurityUtils.getUsuarioId();

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário logado não encontrado"));
    }

    /* ==========================================================
       ===================== CRIAR ==============================
       ========================================================== */

    public TransacaoResponseDTO salvar(TransacaoRequestDTO dto) {

        Usuario usuario = getUsuarioLogado();

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
            throw new ConflitoDeDadosException(
                    "Transferência deve usar endpoint específico"
            );
        }

        Transacao transacao = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .dataHora(LocalDateTime.now())
                .tipo(dto.tipo())
                .categoria(categoria)
                .usuario(usuario)
                .build();

        return toDTO(repository.save(transacao));
    }

    /* ==========================================================
       ===================== LISTAR =============================
       ========================================================== */

    public List<TransacaoResponseDTO> listarDoUsuarioLogado() {

        Long usuarioId = SecurityUtils.getUsuarioId();

        List<Transacao> transacoes =
                repository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);

        if (transacoes.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                    "Nenhuma transação encontrada para o usuário logado"
            );
        }

        return transacoes.stream()
                .map(this::toDTO)
                .toList();
    }

    public TransacaoResponseDTO buscarPorId(Long id) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Transacao transacao = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Transação não encontrada"));

        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new ConflitoDeDadosException(
                    "Acesso negado à transação de outro usuário"
            );
        }

        return toDTO(transacao);
    }

    /* ==========================================================
       ===================== EXCLUIR ============================
       ========================================================== */

    public void deletar(Long id) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Transacao transacao = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Transação não encontrada"));

        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new ConflitoDeDadosException(
                    "Acesso negado à transação de outro usuário"
            );
        }

        repository.delete(transacao);
    }

    /* ==========================================================
       ==================== TRANSFERÊNCIA =======================
       ========================================================== */

    @Transactional
    public void transferir(TransacaoTransferenciaDTO dto) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario origem = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário de origem não encontrado"));

        Usuario destino = usuarioRepository.findByNumeroConta(dto.contaDestino())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Conta de destino não encontrada"));

        if (origem.getId().equals(destino.getId())) {
            throw new ConflitoDeDadosException(
                    "Não é possível transferir para a própria conta"
            );
        }

        BigDecimal saldoOrigem = repository.calcularSaldoUsuario(origem.getId());

        if (saldoOrigem.compareTo(dto.valor()) < 0) {
            throw new ConflitoDeDadosException("Saldo insuficiente");
        }

        LocalDateTime agora = LocalDateTime.now();

        Transacao debito = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .dataHora(agora)
                .tipo(TipoTransacao.TRANSFERENCIA)
                .categoria(CategoriaTransacao.OUTROS)
                .usuario(origem)
                .contaRelacionada(destino)
                .build();

        Transacao credito = Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .dataHora(agora)
                .tipo(TipoTransacao.DEPOSITO)
                .categoria(CategoriaTransacao.OUTROS)
                .usuario(destino)
                .contaRelacionada(origem)
                .build();

        repository.save(debito);
        repository.save(credito);
    }

    /* ==========================================================
       ===================== MAPEAMENTO =========================
       ========================================================== */

    private TransacaoResponseDTO toDTO(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getCategoria(),
                transacao.getUsuario().getId()
        );
    }
}
