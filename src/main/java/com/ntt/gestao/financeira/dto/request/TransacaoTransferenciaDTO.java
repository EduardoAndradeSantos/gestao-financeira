package com.ntt.gestao.financeira.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransacaoTransferenciaDTO(
        @NotNull String contaDestino,
        @NotNull BigDecimal valor,
        String descricao
) {}
