package com.servicios.FoodExpress.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/** Utilitario para generar tokens JWT firmados. */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    /** Configura el utilitario de JWT leyendo la clave secreta y la expiracion en milisegundos. */
    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /**
     * Crea un token firmado HS256 con subject simple (email/username).
     *
     * @param subject identificador del usuario
     * @return token JWT en texto plano
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
