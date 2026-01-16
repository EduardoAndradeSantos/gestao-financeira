package com.ntt.gestao.financeira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração do encoder de senhas da aplicação.
 *
 * Centraliza a definição do algoritmo de criptografia
 * utilizado para armazenar e validar senhas de usuários.
 */
@Configuration
public class PasswordConfig {

    /**
     * Bean responsável por criptografar senhas utilizando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
