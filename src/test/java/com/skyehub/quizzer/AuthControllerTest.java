package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.SignupDto;
import com.skyehub.quizzer.error.ErrorResponse;
import com.skyehub.quizzer.profile.ProfileRepository;
import com.skyehub.quizzer.profile.UserProfile;
import com.skyehub.quizzer.shared.GenericResponse;

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

    private <T> ResponseEntity<T> postSignup(Object requestPayload, Class<T> response) {
        return restTemplate.postForEntity(API_1_0_AUTH_REGISTER, requestPayload, response);
    }

    private static SignupDto signupValidUser() {
        return new SignupDto("me@mail.com", "P4ssword");
    }

    private static SignupDto signupInvalidUser(String email, String password) {
        return new SignupDto(email, password);
    }

    @Test
    @DirtiesContext
    public void signupUser_whenUserIsValid_returnOkStatusCode() {
        SignupDto signupDto = signupValidUser();
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void signupUser_whenUserEmailIsNull_returnBadRequest() {
        SignupDto signupDto = signupInvalidUser(null, "P4ssword");
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void signupUser_whenUserEmailIsInvalid_returnBadRequest() {
        SignupDto signupDto = signupInvalidUser("me.mail", "P4ssword");
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void signupUser_whenUserPasswordIsNull_returnBadRequest() {
        SignupDto signupDto = signupInvalidUser("me@mail.com", null);
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void signupUser_whenUserPasswordIsInvalid_returnBadRequest() {
        SignupDto signupDto = signupInvalidUser("me@mail.com", "Password");
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void signupUser_whenPasswordLengthIsShort_returnBadRequest() {
        SignupDto signupDto = signupInvalidUser("me@mail.com", "P4sswd");
        ResponseEntity<Object> response = postSignup(signupDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void signupUser_whenUserIsValid_createUserProfile() {
        SignupDto signupDto = signupValidUser();
        postSignup(signupDto, String.class);
        assertThat(profileRepository.count()).isEqualTo(1);
    }

    @Test
    public void signupUser_whenUserIsValid_returnSuccessMessage() {
        SignupDto signupDto = signupValidUser();
        ResponseEntity<GenericResponse> response = postSignup(signupDto, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }

    @Test
    public void signupUser_whenUserIsInvalid_returnErrorResponse() {
        SignupDto signupDto = signupInvalidUser(null, null);
        ResponseEntity<ErrorResponse> response = postSignup(signupDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).url()).isEqualTo(API_1_0_AUTH_REGISTER);
    }

    @Test
    public void signupUser_whenUserIsInvalid_returnErrorResponseWithValidationErrors() {
        SignupDto signupDto = signupInvalidUser(null, null);
        ResponseEntity<ErrorResponse> response = postSignup(signupDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).errors().size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void signupUser_whenUserIsValid_hashUserPasswordInDb() {
        SignupDto signupDto = signupValidUser();
        postSignup(signupDto, GenericResponse.class);
        List<UserProfile> users = profileRepository.findAll();
        UserProfile userInDb = users.get(0);
        assertThat(userInDb.getPassword()).isNotEqualTo(signupDto.password());
    }
}
