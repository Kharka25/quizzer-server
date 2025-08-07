package com.skyehub.quizzer.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final UserProfile userProfile;

    public UserPrincipal(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userProfile.getPassword();
    }

    @Override
    public String getUsername() {
        return userProfile.getEmail();
    }
}
