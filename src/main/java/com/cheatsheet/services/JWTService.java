package com.cheatsheet.services;

import com.cheatsheet.entity.Role;
import com.cheatsheet.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {

    private final String SECRET_KEY = "1c5644d5b85c1d0a06f470f95b24347c311226902d63397f7512a33329d2c02e";

    // take email from token
    public String extractUserEmail(String token) {
        return extractClaim (token, Claims::getSubject);
    }

    // take id from token
    public Integer extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = extractAllClaims(token);
        return (int) claims.get("userId");
    }

    // take role from token
    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);
        return Role.valueOf(role);
    }

    // take exp-time
    public Date extractExpiration(String token) { return extractClaim(token , Claims::getExpiration); }

    // check token is exp or not
    public boolean isTokenExpired(String token) { return extractExpiration(token).before(new Date()); }

    // check email from token
    public boolean isValid(String token, UserDetails user){
        String userEmail = extractUserEmail(token);
        return (userEmail.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private <T>T extractClaim(String token , Function<Claims,T> resolver)  {
        Claims claims= extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserEntity userEntity) {
        String role = userEntity.getRole().name();
        return Jwts.builder()
                .subject(userEntity.getEmail())
                .claim("role", role)
                .claim("userId", userEntity.getId())
                .claim("status", userEntity.getIsBanned())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
