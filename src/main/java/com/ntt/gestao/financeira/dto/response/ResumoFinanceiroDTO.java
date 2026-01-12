package com.ntt.gestao.financeira.dto.response;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoAtual
) {}
