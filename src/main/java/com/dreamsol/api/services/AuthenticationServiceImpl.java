package com.dreamsol.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.JwtRequest;
import com.dreamsol.api.dto.JwtResponse;
import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.entities.RefreshToken;
import com.dreamsol.api.security.JwtUtility;

import jakarta.validation.Valid;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    UserService userService;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtUtility helper;
    @Autowired
    RefreshTokenService refreshTokenService;
    

    @Override
    public ResponseEntity<JwtResponse> getToken(RefreshTokenRequest request) {
        RefreshToken dbToken =refreshTokenService.fromToken(request);
        String userName=dbToken.getUserEmail();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        String jwtToken = refreshTokenService.createJwtToken(request, userDetails);
        if (jwtToken == null) {
            throw new RuntimeException("Invalid Refresh Token or Token has been Expired");
        }
        JwtResponse response = JwtResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(request).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> createUser(@Valid UserDto user, MultipartFile file,String path) throws Exception {
        return userService.addUser(user, file, path);
    }

    @Override
    public ResponseEntity<JwtResponse> login(JwtRequest request) {
        this.doAuthenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = this.helper.generateToken(userDetails);
        RefreshToken refreshtoken = refreshTokenService.createToken(userDetails.getUsername());

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(dtoUtility.toRefreshTokenRequest(refreshtoken))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password !!");
        }
    }

}
