package com.dreamsol.api.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dreamsol.api.entities.User;
import com.dreamsol.api.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtility {
    @Autowired
    UserRepository userRepo;

    @Value("${jwt.token.validity}")
    int jwtTokenValidity;

    private static final String secret = "kjbwdlubdcueuhcqjhbubcluecljhqdcuvdcqHSHSUSuheuycjqhdjNJSUSSUYVSYUVYTCCTRXKIOUTWKJHFTRCUYVFugduywvecuyqevcjhquyvcjqhdcyvcubybu";
    private static final Key key= new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());

    public String getUsernameFormToken(String token) {
        System.out.println("Email : JwtFilter class: "+getEmailfromToken(token));
        return getEmailfromToken(token);
    }
    public int getIdFormToken(String token) {
        Claims claims=getAllClaimsFromToken(token);
        return (int)claims.get("Id");
    }
    public String getRoleFormToken(String token) {
        Claims claims=getAllClaimsFromToken(token);
        return (String)claims.get("Role");
    }
    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
         Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public String getEmailfromToken(String token){
        Claims claims=getAllClaimsFromToken(token);
        return (String)claims.get("Email");
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    // generate token for user
    public String generateToken(UserDetails userDetails) {
        User user = userRepo.findByEmail(userDetails.getUsername()).get();
        Map<String, Object> payload = new HashMap<>();
        payload.put("Id", user.getID());
        payload.put("Name", user.getName());
        payload.put("Email", user.getEmail());
        payload.put("Mobile-Number", user.getMobile());
        payload.put("Role", user.getUsertype().getUserTypeName());
        payload.put("Permission", user.getPermission().getPermission());
        String subject = user.getEmail();
        return doGenerateToken(payload, subject);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> payload, String subject) {
        int duration = jwtTokenValidity * 60 * 1000; // minutes to miliseconds conversion
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(payload).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + duration)) // minutes to mili seconds
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
 

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFormToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
