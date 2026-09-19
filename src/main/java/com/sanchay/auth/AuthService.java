package com.sanchay.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sanchay.exception.UnauthorizedException;

@Service
public class AuthService {

    private final JwtService jwtService;

    @Value("${APP_USERNAME}")
    private String configuredUsername;

    @Value("${APP_PASSWORD}")
    private String configuredPassword;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {

        if (!configuredUsername.equals(username)
                || !configuredPassword.equals(password)) {

            throw new UnauthorizedException("Invalid username or password");
        }

        return jwtService.generateToken(username);
    }
}