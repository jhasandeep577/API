package com.dreamsol.api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.JwtRequest;
import com.dreamsol.api.dto.JwtResponse;
import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.dto.UserDto;

import jakarta.validation.Valid;

@Service
public interface AuthenticationService {
    ResponseEntity<JwtResponse> getToken(@RequestBody RefreshTokenRequest request);

    ResponseEntity<UserDto> createUser(@Valid @RequestPart("UserDto") UserDto user,
            @RequestParam("image") MultipartFile file,String path) throws Exception;

    ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request);

}
