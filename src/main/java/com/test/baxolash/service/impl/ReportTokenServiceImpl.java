package com.test.baxolash.service.impl;

import com.test.baxolash.exception.BusinessException;
import com.test.baxolash.service.ReportTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class ReportTokenServiceImpl implements ReportTokenService {

    private static final String CLAIM_REQUEST_ID = "rid";
    private static final long VALIDITY_MS = 30L * 24 * 60 * 60 * 1000; // 30 дней

    private final SecretKey key;

    public ReportTokenServiceImpl(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public String generateToken(String evaluationRequestId) {
        if (evaluationRequestId == null || evaluationRequestId.isBlank()) {
            throw new BusinessException("requestId обязателен для токена отчёта");
        }
        Date now = new Date();
        Date expiry = new Date(now.getTime() + VALIDITY_MS);
        return Jwts.builder()
                .setSubject("report")
                .claim(CLAIM_REQUEST_ID, evaluationRequestId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String validateAndGetRequestId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(CLAIM_REQUEST_ID, String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
