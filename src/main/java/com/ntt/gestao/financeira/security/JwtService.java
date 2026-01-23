package com.ntt.gestao.financeira.security;

import com.ntt.gestao.financeira.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "minha-chave-secreta-jwt-1234567890";
    private static final long EXPIRACAO = 1000 * 60 * 60; // 1 hora

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("usuarioId", usuario.getId())
                .claim("numeroConta", usuario.getNumeroConta())
                .claim("role", usuario.getRole().name()) // 👈 AQUI
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValido(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUsuarioId(String token) {
        return parseToken(token)
                .getBody()
                .get("usuarioId", Long.class);
    }

    public String getEmail(String token) {
        return parseToken(token)
                .getBody()
                .getSubject();
    }

    public String getNumeroConta(String token) {
        return parseToken(token)
                .getBody()
                .get("numeroConta", String.class);
    }

    public String getRole(String token) {
        return parseToken(token)
                .getBody()
                .get("role", String.class);
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}
