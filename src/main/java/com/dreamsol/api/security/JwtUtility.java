package com.dreamsol.api.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 18000=18sec
    private String secret = "kjbwdlubdcueuhcqjhbubcluecljhqdcuvdcqHSHSUSuheuycjqhdjNJSUSSUYVSYUVYTCCTRXKIOUTWKJHFTRCUYVFugduywvecuyqevcjhquyvcjqhdcyvcubybu";

    public String getUsernameFormToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
    }

    // generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY ))    // 18sec*60*10 = 180m or 3hr
                .signWith(getSigninKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    public String doGenerateRefreshToken(Map<String, Object> extraclaims, String subject) {            // subject is username

        return Jwts.builder().setClaims(extraclaims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 60 * 1000))   // 18sec*60*1000 = 18000m or 300hr or approx 12Days
                .signWith(getSigninKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSigninKey() {
        byte[] key = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFormToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
