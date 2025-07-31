package com.skyehub.quizzer;

import com.skyehub.quizzer.dto.SignupDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void signupUser_whenUserIsValid_returnOkStatusCode() {
        SignupDto signupDto = new SignupDto("me@mail.com", "P4ssword");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/1.0/auth/register", signupDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
