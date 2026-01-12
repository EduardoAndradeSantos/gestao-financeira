package com.ntt.gestao.financeira.dto.response;

public record MoedaCambioDTO(
        String simbolo,
        String nome,
        String tipo_moeda
) {}