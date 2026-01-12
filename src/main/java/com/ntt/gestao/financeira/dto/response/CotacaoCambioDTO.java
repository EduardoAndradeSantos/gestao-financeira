package com.ntt.gestao.financeira.dto.response;

import java.math.BigDecimal;

public record CotacaoCambioDTO(
        String data,
        BigDecimal cotacaoCompra,
        BigDecimal cotacaoVenda
) {}