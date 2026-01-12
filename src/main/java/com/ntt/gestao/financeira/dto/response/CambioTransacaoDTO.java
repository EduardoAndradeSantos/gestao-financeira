package com.ntt.gestao.financeira.dto.response;

import java.math.BigDecimal;

public record CambioTransacaoDTO(
        String moeda,
        BigDecimal cotacao,
        BigDecimal valorConvertido
) {}