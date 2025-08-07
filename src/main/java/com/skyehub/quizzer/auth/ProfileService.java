package com.skyehub.quizzer.auth;

import com.skyehub.quizzer.dto.AuthDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService implements UserDetailsService {
    private static final String USER_NOT_FOUND_MSG = "user with email %s not found";

    @Autowired
    AuthenticationManager authManager;

    BCryptPasswordEncoder passwordEncoder;

    ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.profileRepository = profileRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> userProfile = profileRepository.findByEmail(email);
        if (userProfile.isEmpty()) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }
        return new UserPrincipal(userProfile.get());
    }

    public void create(AuthDto authDto) {
        UserProfile userProfile = new UserProfile(authDto.email(), authDto.password());
        userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
        profileRepository.save(userProfile);
    }

    public void login(AuthDto authDto) throws AuthenticationException {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                authDto.email(),
                authDto.password()
        ));
    }
}
