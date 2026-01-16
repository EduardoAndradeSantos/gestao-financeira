package com.ntt.gestao.financeira.config;

import com.ntt.gestao.financeira.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança da aplicação.
 *
 * Define:
 * - Estratégia de autenticação (JWT)
 * - Quais endpoints são públicos
 * - Política de sessão (stateless)
 * - Cadeia de filtros de segurança do Spring Security
 */
@Configuration
@EnableMethodSecurity // Permite uso de @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    // Filtro responsável por extrair e validar o JWT das requisições
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * Esse método substitui a antiga abordagem com WebSecurityConfigurerAdapter
     * (deprecated nas versões mais novas do Spring Security).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desabilita CSRF pois a API é stateless e não utiliza sessão
                .csrf(csrf -> csrf.disable())

                // Define que a aplicação não mantém estado de sessão
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Define regras de autorização dos endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (login e documentação)
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Qualquer outra requisição exige autenticação
                        .anyRequest().authenticated()
                )

                // Adiciona o filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Desabilita autenticação baseada em formulário
                .formLogin(form -> form.disable())

                // Desabilita autenticação HTTP Basic
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
