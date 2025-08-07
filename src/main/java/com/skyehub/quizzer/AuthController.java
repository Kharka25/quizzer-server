package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.AuthDto;
import com.skyehub.quizzer.auth.ProfileService;
import com.skyehub.quizzer.shared.GenericResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/1.0/auth")
public class AuthController {

    @Autowired
    ProfileService profileService;

    @PostMapping("/register")
    private GenericResponse signUp(@Valid @RequestBody AuthDto authDto) {
        profileService.create(authDto);
        return new GenericResponse("Profile created");
    }

    @PostMapping("/login")
    private GenericResponse login(@Valid @RequestBody AuthDto authDto) {
        profileService.login(authDto);
        return new GenericResponse();
    }
}
