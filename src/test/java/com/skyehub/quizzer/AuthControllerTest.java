package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.AuthDto;
import com.skyehub.quizzer.error.ErrorResponse;
import com.skyehub.quizzer.error.FieldErrorResponse;
import com.skyehub.quizzer.auth.ProfileRepository;
import com.skyehub.quizzer.auth.UserProfile;
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
    public static final String API_1_0_AUTH_LOGIN = "/api/1.0/auth/login";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProfileRepository profileRepository;

    private <T> ResponseEntity<T> postLogin(Object requestPayload, Class<T> response) {
        return restTemplate.postForEntity(API_1_0_AUTH_LOGIN, requestPayload, response);
    }

    private <T> ResponseEntity<T> postSignup(Object requestPayload, Class<T> response) {
        return restTemplate.postForEntity(API_1_0_AUTH_REGISTER, requestPayload, response);
    }

    private static AuthDto authValidUser() {
        return new AuthDto("me@mail.com", "P4ssword");
    }

    private static AuthDto authInvalidUser(String email, String password) {
        return new AuthDto(email, password);
    }

    @Test
    @DirtiesContext
    public void userSignup_whenUserIsValid_returnOkStatusCode() {
        AuthDto authDto = authValidUser();
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void userSignup_whenUserEmailIsNull_returnBadRequest() {
        AuthDto authDto = authInvalidUser(null, "P4ssword");
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userSignup_whenUserEmailIsInvalid_returnBadRequest() {
        AuthDto authDto = authInvalidUser("me.mail", "P4ssword");
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userSignup_whenUserPasswordIsNull_returnBadRequest() {
        AuthDto authDto = authInvalidUser("me@mail.com", null);
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userSignup_whenUserPasswordIsInvalid_returnBadRequest() {
        AuthDto authDto = authInvalidUser("me@mail.com", "Password");
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userSignup_whenPasswordLengthIsShort_returnBadRequest() {
        AuthDto authDto = authInvalidUser("me@mail.com", "P4sswd");
        ResponseEntity<Object> response = postSignup(authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userSignup_whenUserIsInvalid_returnErrorResponse() {
        AuthDto authDto = authInvalidUser(null, null);
        ResponseEntity<ErrorResponse> response = postSignup(authDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).url()).isEqualTo(API_1_0_AUTH_REGISTER);
    }

    @Test
    public void userSignup_whenUserIsInvalid_returnErrorResponseWithValidationErrors() {
        AuthDto authDto = authInvalidUser(null, null);
        ResponseEntity<ErrorResponse> response = postSignup(authDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).errors().size()).isEqualTo(2);
    }

    @Test
    public void userSignup_whenUserIsValid_createUserProfile() {
        AuthDto authDto = authValidUser();
        postSignup(authDto, String.class);
        assertThat(profileRepository.count()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void userSignup_whenUserIsValid_returnSuccessMessage() {
        AuthDto authDto = authValidUser();
        ResponseEntity<GenericResponse> response = postSignup(authDto, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }


    @Test
    public void userSignup_whenUserIsValid_hashUserPasswordInDb() {
        AuthDto authDto = authValidUser();
        postSignup(authDto, GenericResponse.class);
        List<UserProfile> users = profileRepository.findAll();
        UserProfile userInDb = users.get(0);
        assertThat(userInDb.getPassword()).isNotEqualTo(authDto.password());
    }

    @Test
    public void userLogin_whenLoginCredentialsIsNull_returnBadRequest() {
        AuthDto authDto = authInvalidUser(null, null);
        ResponseEntity<Object> response = restTemplate.postForEntity(API_1_0_AUTH_LOGIN, authDto, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void userLogin_whenLoginIsInvalid_returnErrorResponseWithValidationErrors() {
        AuthDto authDto = authInvalidUser(null, null);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(API_1_0_AUTH_LOGIN, authDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).errors().size()).isEqualTo(2);
    }

    @Test
    public void userLogin_whenEmailIsInvalid_returnErrorResponseWithEmailValidationError() {
        AuthDto authDto = authInvalidUser(null, "P4ssword");
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(API_1_0_AUTH_LOGIN, authDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).errors()).containsAnyOf(new FieldErrorResponse("email", "email is required"));
    }

    @Test
    public void userLogin_whenPasswordIsInvalid_returnErrorResponseWithPasswordValidationError() {
        AuthDto authDto = authInvalidUser("me@mail.com", null);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(API_1_0_AUTH_LOGIN, authDto, ErrorResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).errors()).containsAnyOf(new FieldErrorResponse("password", "password is required"));
    }

    @Test
    public void userLogin_whenUserIsNotAuthenticated_returnForbiddenStatusCode() {
        AuthDto authDto = authValidUser();
        postSignup(authDto, Object.class);
        AuthDto invalidAuth = authInvalidUser("me2@mail.com", "P4ssword");
        ResponseEntity<ErrorResponse> response = postLogin(invalidAuth, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void userLogin_whenUserIsNotAuthenticated_returnInvalidCredentialsErrorMessage() {
        AuthDto authDto = authValidUser();
        postSignup(authDto, Object.class);
        AuthDto invalidAuth = authInvalidUser("me@mail.com", "Password12345");
        ResponseEntity<ErrorResponse> response = postLogin(invalidAuth, ErrorResponse.class);
       assertThat(Objects.requireNonNull(response.getBody()).message()).isEqualTo("Invalid credentials");
    }
}
