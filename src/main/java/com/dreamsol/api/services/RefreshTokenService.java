package com.dreamsol.api.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.entities.RefreshToken;

@Service
public interface RefreshTokenService {
    boolean validRefreshToken(RefreshTokenRequest token);

    boolean isTokenExpired(RefreshTokenRequest token);

    RefreshToken createToken(String user_id);

    String createJwtToken(RefreshTokenRequest token,UserDetails details);
    
    RefreshToken fromToken(RefreshTokenRequest token);
}
