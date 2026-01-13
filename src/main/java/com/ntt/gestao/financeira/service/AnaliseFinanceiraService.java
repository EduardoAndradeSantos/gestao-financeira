package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AnaliseFinanceiraService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public AnaliseFinanceiraService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário logado não encontrado"));
    }

    public Map<String, BigDecimal> resumoFinanceiro() {

        Usuario usuario = getUsuarioLogado();
        Long usuarioId = usuario.getId();

        BigDecimal saldo = transacaoRepository.calcularSaldoUsuario(usuarioId);
        BigDecimal receitas = transacaoRepository.totalReceitas(usuarioId);
        BigDecimal despesas = transacaoRepository.totalDespesas(usuarioId);

        return Map.of(
                "saldo", saldo,
                "receitas", receitas,
                "despesas", despesas
        );
    }

    public List<DespesaPorCategoriaDTO> despesasPorCategoria() {

        Usuario usuario = getUsuarioLogado();
        return transacaoRepository.totalDespesasPorCategoria(usuario.getId());
    }
}
