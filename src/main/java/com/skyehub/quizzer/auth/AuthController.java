package com.skyehub.quizzer.auth;

import com.skyehub.quizzer.dto.AuthDto;
import com.skyehub.quizzer.shared.GenericResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/1.0/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    private GenericResponse signUp(@Valid @RequestBody AuthDto authDto) {
        authService.create(authDto);
        return new GenericResponse("Profile created");
    }

    @PostMapping("/login")
    private ResponseEntity<String> login(@Valid @RequestBody AuthDto authDto) {
        authService.login(authDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Access Token")
                .body("success");
    }
}
