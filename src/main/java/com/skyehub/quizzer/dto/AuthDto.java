package com.skyehub.quizzer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthDto(
        @Email(message = "invalid email")
        @NotBlank(message = "email is required")
        String email,
        @NotBlank(message = "password is required")
        @Pattern(message = "password must contain number and uppercase character", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")
        @Size(min = 8, max = 255, message = "password is must be over 8 characters")
        String password) {
}
