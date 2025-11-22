package com.example.ecommerce.service;

import com.example.ecommerce.dto.auth.LoginRequestDTO;
import com.example.ecommerce.dto.auth.LoginResponseDTO;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(authentication.getName());
            return new LoginResponseDTO(token, authentication.getName());

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}
