package com.ntt.gestao.financeira.dto.request;

import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoRequestDTO(
        @NotBlank String descricao,
        @NotNull BigDecimal valor,
        @NotNull TipoTransacao tipo,
        CategoriaTransacao categoria,
        @NotNull Long usuarioId
) {}
