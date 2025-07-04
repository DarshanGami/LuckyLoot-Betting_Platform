package com.example.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public boolean validateToken(String token) {
        System.out.println("=== VALIDATE TOKEN START ===");
        System.out.println("Token: " + token);
        System.out.println("SECRET_KEY: " + SECRET_KEY);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("Token successfully validated!");
            System.out.println("Subject (userId): " + claims.getSubject());
            System.out.println("Email: " + claims.get("email"));
            System.out.println("Issued at: " + claims.getIssuedAt());
            System.out.println("Expires at: " + claims.getExpiration());

            return true;

        } catch (Exception e) {
            System.out.println("!!! JWT VALIDATION FAILED !!!");
            e.printStackTrace();
            return false;
        }
    }


    public String extractUserId(String token) {
        return getClaims(token).getSubject();    // ✅ From subject
    }

    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);    // ✅ From custom claim
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
