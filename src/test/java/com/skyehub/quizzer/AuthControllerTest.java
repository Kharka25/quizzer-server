package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.SignupDto;
import com.skyehub.quizzer.profile.ProfileRepository;
import com.skyehub.quizzer.profile.UserProfile;
import com.skyehub.quizzer.shared.GenericResponse;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    public static final String API_1_0_AUTH_REGISTER = "/api/1.0/auth/register";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProfileRepository profileRepository;

    private static SignupDto signUpUser() {
        return new SignupDto("me@mail.com", "P4ssword");
    }

    @Test
    @DirtiesContext
    public void signupUser_whenUserIsValid_returnOkStatusCode() {
        SignupDto signupDto = signUpUser();
        ResponseEntity<Object> response = restTemplate.postForEntity(API_1_0_AUTH_REGISTER, signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void signupUser_whenUserIsValid_createUserProfile() {
        SignupDto signupDto = signUpUser();
        restTemplate.postForEntity(API_1_0_AUTH_REGISTER, signupDto, String.class);
        assertThat(profileRepository.count()).isEqualTo(1);
    }

    @Test
    public void signupUser_whenUserIsValid_returnSuccessMessage() {
        SignupDto signupDto = signUpUser();
        ResponseEntity<GenericResponse> response = restTemplate.postForEntity(API_1_0_AUTH_REGISTER, signupDto, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void signupUser_whenUserIsValid_hashUserPasswordInDb() {
        SignupDto signupDto = signUpUser();
        restTemplate.postForEntity(API_1_0_AUTH_REGISTER, signupDto, GenericResponse.class);
        List<UserProfile> users = profileRepository.findAll();
        UserProfile userInDb = users.get(0);
        assertThat(userInDb.getPassword()).isNotEqualTo(signupDto.password());
    }
}
