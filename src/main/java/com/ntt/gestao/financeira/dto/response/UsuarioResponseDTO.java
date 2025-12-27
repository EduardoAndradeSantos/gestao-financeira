package com.ntt.gestao.financeira.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        String endereco,
        String email
) {}
