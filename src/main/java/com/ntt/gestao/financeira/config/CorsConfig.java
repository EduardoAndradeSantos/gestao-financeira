package com.ntt.gestao.financeira.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração global de CORS (Cross-Origin Resource Sharing).
 *
 * Responsável por permitir que aplicações frontend
 * hospedadas em outros domínios (ex: Angular em localhost:4200)
 * consigam acessar esta API Spring Boot.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Define as regras de CORS para todos os endpoints da aplicação.
     *
     * Este método é chamado automaticamente pelo Spring
     * durante a inicialização da aplicação.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**") // Aplica o CORS a todos os endpoints da API
                .allowedOrigins("http://localhost:4200") // Permite requisições apenas deste frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite qualquer header na requisição
                .allowCredentials(true); // Permite envio de cookies ou headers de autenticação
    }
}
