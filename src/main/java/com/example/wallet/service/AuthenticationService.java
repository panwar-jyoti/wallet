package com.example.wallet.service;

import com.example.wallet.config.SecurityConfig;
import com.example.wallet.exception.CustomException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityConfig.JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String registerUser(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .registrationStatus(User.RegistrationStatus.PENDING)
                .offlinePaymentEnabled(false)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return jwtToken;
    }

    public String authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No such user found"));
        var jwtToken = jwtService.generateToken(user);
        return jwtToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        String username;
        String name;
        String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AuthRequest {
        String username;
        String password;
    }
}