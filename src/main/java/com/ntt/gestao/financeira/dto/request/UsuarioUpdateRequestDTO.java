package com.ntt.gestao.financeira.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequestDTO(

        @NotBlank(message = "Nome não pode estar em branco")
        String nome,

        @NotBlank(message = "Endereço não pode estar em branco")
        String endereco
) {}
