package com.ntt.gestao.financeira.dto.response;

import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        @NotNull LocalDateTime dataHora,
        TipoTransacao tipo,
        CategoriaTransacao categoria,
        Long usuarioId,
        CambioTransacaoDTO cambio
) {}
