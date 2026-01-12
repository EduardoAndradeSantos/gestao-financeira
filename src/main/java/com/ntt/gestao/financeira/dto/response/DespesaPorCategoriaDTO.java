package com.ntt.gestao.financeira.dto.response;

import com.ntt.gestao.financeira.entity.CategoriaTransacao;
import java.math.BigDecimal;

public record DespesaPorCategoriaDTO(
        CategoriaTransacao categoria,
        BigDecimal total
) {}
