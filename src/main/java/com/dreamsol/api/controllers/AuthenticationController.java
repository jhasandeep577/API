package com.dreamsol.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.JwtRequest;
import com.dreamsol.api.dto.JwtResponse;
import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.services.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "To Fetch JWT Token or Create User")
public class AuthenticationController {

    @Autowired
    AuthenticationService authService;
    @Value("${project.image}")
    String path;

    @GetMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        return authService.login(request);
    }

    @PostMapping(path = "/create-User", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(@Valid UserDto user, MultipartFile file) throws Exception {
        return authService.createUser(user, file, path);
    }

    @GetMapping("/getJwtToken-from-RefreshToken")
    public ResponseEntity<JwtResponse> getToken(@RequestBody RefreshTokenRequest request) {
        return authService.getToken(request);
    }
}