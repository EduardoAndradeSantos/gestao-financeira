package com.ntt.gestao.financeira.dto.response;

import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        TipoTransacao tipo,
        CategoriaTransacao categoria,
        Long usuarioId
) {}
