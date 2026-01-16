package com.ntt.gestao.financeira.dto.response;

import java.math.BigDecimal;

public record SaldoResponseDTO(
        String numeroConta,
        BigDecimal saldo
) {}
