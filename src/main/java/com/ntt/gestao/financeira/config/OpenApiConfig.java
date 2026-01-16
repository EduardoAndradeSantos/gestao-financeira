package com.ntt.gestao.financeira.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração global do OpenAPI / Swagger.
 *
 * Este arquivo define:
 * - Informações gerais da API (nome, versão e descrição)
 * - O esquema de segurança utilizado (JWT Bearer Token)
 * - A exigência de autenticação para os endpoints documentados
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Gestão Financeira", // Nome exibido no Swagger UI
                version = "v1",                  // Versão da API
                description = "API de Gestão Financeira com autenticação JWT"
        ),
        // Define que a API utiliza autenticação via Bearer Token (JWT)
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",                   // Nome do esquema de segurança
        type = SecuritySchemeType.HTTP,        // Tipo HTTP
        scheme = "bearer",                     // Padrão Bearer
        bearerFormat = "JWT"                   // Formato do token utilizado
)
public class OpenApiConfig {
}