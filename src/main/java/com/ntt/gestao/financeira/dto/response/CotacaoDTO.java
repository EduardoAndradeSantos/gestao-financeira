package com.ntt.gestao.financeira.dto.response;

import java.math.BigDecimal;

public record CotacaoDTO(
        BigDecimal paridade_compra,
        BigDecimal paridade_venda,
        BigDecimal cotacao_compra,
        BigDecimal cotacao_venda,
        String data_hora_cotacao,
        String tipo_boletim
) {}
