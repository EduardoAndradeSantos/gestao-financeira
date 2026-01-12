package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import com.ntt.gestao.financeira.dto.response.ResumoFinanceiroDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnaliseFinanceiraService {

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;

    public AnaliseFinanceiraService(
            UsuarioRepository usuarioRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public ResumoFinanceiroDTO resumoPorConta(String numeroConta) {

        Usuario usuario = usuarioRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        BigDecimal receitas = transacaoRepository.totalReceitas(usuario.getId());
        BigDecimal despesas = transacaoRepository.totalDespesas(usuario.getId());
        BigDecimal saldo = receitas.subtract(despesas);

        return new ResumoFinanceiroDTO(receitas, despesas, saldo);
    }

    public List<DespesaPorCategoriaDTO> despesasPorCategoria(String numeroConta) {

        Usuario usuario = usuarioRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return transacaoRepository.totalDespesasPorCategoria(usuario.getId());
    }
}
