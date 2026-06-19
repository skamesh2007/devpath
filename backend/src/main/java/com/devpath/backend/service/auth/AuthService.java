package com.devpath.backend.service.auth;

import com.devpath.backend.dto.auth.*;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.UserRepository;
import com.devpath.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check username uniqueness first
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }
        // Check email uniqueness separately so frontend can show the right field error
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return buildResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier();

        // Try to find user by username OR email and give specific not-found messages
        User user;
        boolean looksLikeEmail = identifier.contains("@");

        if (looksLikeEmail) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No account found with email '" + identifier + "'"));
        } else {
            // Try username first; fall back to email in case user types email without @
            user = userRepository.findByUsername(identifier)
                    .or(() -> userRepository.findByEmail(identifier))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No account found with username '" + identifier + "'"));
        }

        // Authenticate — throws BadCredentialsException on wrong password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password for '" + user.getUsername() + "'");
        }

        String token = jwtService.generateToken(user);

        return buildResponse(token, user);
    }

    public AuthResponse getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return buildResponse(token, user);
    }

    private AuthResponse buildResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse updateProfile(String currentUsername, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newUsername = request.getUsername().trim();

        // Only check uniqueness if the username is actually changing
        if (!newUsername.equals(currentUsername) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Username '" + newUsername + "' is already taken");
        }

        user.setUsername(newUsername);
        user.setName(request.getName() != null ? request.getName().trim() : null);
        user.setBio(
                request.getBio() != null
                        ? request.getBio().trim()
                        : null
        );
        userRepository.save(user);

        // Issue a fresh token because the subject (username) may have changed
        String token = jwtService.generateToken(user);
        return buildResponse(token, user);
    }

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void changePassword(
            ChangePasswordRequest request
    ) {

        User user = getCurrentUser();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (request.getNewPassword() == null
                || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

}