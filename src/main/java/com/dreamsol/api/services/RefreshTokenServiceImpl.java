package com.dreamsol.api.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.entities.RefreshToken;
import com.dreamsol.api.repositories.RefreshTokenRepo;
import com.dreamsol.api.security.JwtUtility;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    RefreshTokenRepo repo;
    @Autowired
    JwtUtility jwtUtility;
    @Override
    public boolean validRefreshToken(RefreshTokenRequest token) {
       if(repo.findByRefreshToken(token.getRefreshToken()).isPresent()==true)
        return true;
       else
        return false;
    }

    @Override
    public boolean isTokenExpired(RefreshTokenRequest token) {
       if(this.validRefreshToken(token)==true){
         RefreshToken dbToken=repo.findByRefreshToken(token.getRefreshToken()).get();
         if(Instant.now().compareTo(dbToken.getExpirationTime())>0){
            return true;
         }else{
            return false;
         }
       }else
       return false;
    }

    @Override
    public RefreshToken createToken(String user_mail) {
        boolean bool=repo.findByUserEmail(user_mail).isPresent();
        int refTokenId=0;
        if(bool){
            refTokenId=repo.findByUserEmail(user_mail).get().getId();
        }
        RefreshToken token = new RefreshToken();
        token.setExpirationTime(Instant.now().plusSeconds(100));
        token.setRefreshToken(UUID.randomUUID().toString()+UUID.randomUUID().toString()+UUID.randomUUID().toString());
        token.setUserEmail(user_mail);
        if(refTokenId>0){
            token.setId(refTokenId);
        }
        return repo.save(token);
    }

    @Override
    public String createJwtToken(RefreshTokenRequest token,UserDetails details) {
        String newToken=null;
       if(this.isTokenExpired(token)==false){
          return jwtUtility.generateToken(details);
       }else{
        return newToken;
       }
    }
    public RefreshToken fromToken(RefreshTokenRequest reqToken){
        boolean b=this.validRefreshToken(reqToken);
        if(b){
          return repo.findByRefreshToken(reqToken.getRefreshToken()).get();
        }else{
           return new RefreshToken();
        }
    }
    


}
