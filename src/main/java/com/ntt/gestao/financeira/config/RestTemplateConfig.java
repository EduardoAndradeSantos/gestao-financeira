package com.ntt.gestao.financeira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate.
 *
 * Responsável por disponibilizar um cliente HTTP
 * para consumo de APIs externas a partir da aplicação.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean do RestTemplate.
     *
     * Permite realizar chamadas HTTP (GET, POST, PUT, DELETE)
     * para serviços externos de forma simples.
     *
     * Pode ser injetado em Services sempre que a aplicação
     * precisar integrar com outras APIs.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
