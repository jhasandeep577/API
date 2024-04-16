package com.dreamsol.api.controllers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.JwtRequest;
import com.dreamsol.api.dto.JwtResponse;
import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.security.JwtUtility;
import com.dreamsol.api.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "To Fetch JWT Token from username And Password or Create User")
public class AuthenticationController {
    @Autowired
    UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtUtility helper;
    @Value("${project.image}")
    String path;


    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        this.doAuthenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = this.helper.generateToken(userDetails);
        String refreshToken=this.helper.doGenerateRefreshToken(new HashMap<String,Object>(), userDetails.getUsername());
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(path = "/create-User", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(@Valid @RequestPart("UserDto") UserDto user,
            @RequestParam("image") MultipartFile file) throws Exception {
        return userService.addUser(user, file, path);
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

@GetMapping("/getJwtToken-from-RefreshToken")
    public ResponseEntity<JwtResponse> getToken(@RequestBody RefreshTokenRequest request) {
        String userName=this.helper.getUsernameFormToken(request.getToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        String token = this.helper.generateToken(userDetails);
    // String refreshToken=this.helper.doGenerateRefreshToken(new HashMap<String,Object>(), userDetails.getUsername());
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(request.getToken()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}