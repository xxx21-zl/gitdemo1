package com.example.demo1.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类：签发 / 校验 token
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationSeconds) {
        // HS256 要求密钥长度 >= 256 bit（32 字节）
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationSeconds * 1000;
    }

    /**
     * 签发 token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 token，失败或过期会抛出 {@link BusinessJwtException}
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessJwtException("token 无效或已过期");
        }
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }

    /**
     * JWT 解析失败时抛出，避免 util 依赖业务异常包
     */
    public static class BusinessJwtException extends RuntimeException {
        public BusinessJwtException(String message) {
            super(message);
        }
    }
}
