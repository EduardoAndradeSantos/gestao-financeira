package com.ntt.gestao.financeira.dto.request;

import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoPorContaRequestDTO(
        String descricao,
        BigDecimal valor,
        LocalDate data,
        TipoTransacao tipo,
        CategoriaTransacao categoria,
        String numeroConta
) {}



