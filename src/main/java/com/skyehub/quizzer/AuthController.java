package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.SignupDto;
import com.skyehub.quizzer.profile.ProfileService;
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
    private GenericResponse signUp(@Valid @RequestBody SignupDto signupDto) {
        profileService.create(signupDto);
        return new GenericResponse("Profile created");
    }
}
