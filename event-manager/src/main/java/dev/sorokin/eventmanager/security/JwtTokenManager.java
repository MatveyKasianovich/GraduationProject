package dev.sorokin.eventmanager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {
    private final SecretKey key = Keys.hmacShaKeyFor(
            "74cf29aeb2a65032d8e3c252883040648054f42905d3f2ad80e9dfce24d36c1b".getBytes()
    );

    private static final long EXPIRATION_TIME=86400000;

    public String generateToken(String login){
        return Jwts
                .builder()
                .subject(login)
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .compact();

    }

    public String getLoginFromJwt(String jwt){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}