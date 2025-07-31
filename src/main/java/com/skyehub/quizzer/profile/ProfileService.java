package com.skyehub.quizzer.profile;

import com.skyehub.quizzer.dto.SignupDto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    BCryptPasswordEncoder passwordEncoder;

    ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.profileRepository = profileRepository;
    }

    public void create(SignupDto signupDto) {
        UserProfile userProfile = new UserProfile(signupDto.email(), signupDto.password());
        userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
        profileRepository.save(userProfile);
    }
}
