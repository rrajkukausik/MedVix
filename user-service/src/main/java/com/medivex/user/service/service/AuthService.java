package com.medivex.user.service.service;

import com.medivex.user.service.dto.JwtResponseDto;
import com.medivex.user.service.dto.LoginRequestDto;
import com.medivex.user.service.dto.UserRegistrationDto;
import com.medivex.user.service.dto.UserProfileDto;
import com.medivex.user.service.entity.User;
import com.medivex.user.service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update last login
            userService.updateLastLogin(user.getUsername());
            
            // Generate tokens
            String token = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            
            log.info("Login successful for user: {}", user.getUsername());
            
            return JwtResponseDto.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toSet()))
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
                    
        } catch (DisabledException e) {
            log.warn("Login failed - account disabled for user: {}", loginRequest.getUsernameOrEmail());
            throw new RuntimeException("Account is disabled");
        } catch (LockedException e) {
            log.warn("Login failed - account locked for user: {}", loginRequest.getUsernameOrEmail());
            throw new RuntimeException("Account is locked");
        } catch (BadCredentialsException e) {
            log.warn("Login failed - invalid credentials for user: {}", loginRequest.getUsernameOrEmail());
            userService.handleFailedLogin(loginRequest.getUsernameOrEmail());
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
    
    public JwtResponseDto refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token is not a refresh token");
        }
        
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        
        String newToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        log.info("Token refreshed successfully for user: {}", username);
        
        return JwtResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtUtil.getExpirationTime())
                .build();
    }
    
    public void logout(String token) {
        log.info("Logging out user");
        
        if (jwtUtil.validateToken(token)) {
            tokenBlacklistService.blacklistToken(token);
            log.info("User logged out successfully");
        } else {
            log.warn("Invalid token provided for logout");
        }
    }
    
    public UserProfileDto register(UserRegistrationDto registrationDto) {
        log.info("Registering new user: {}", registrationDto.getUsername());
        
        UserProfileDto userProfile = userService.registerUser(registrationDto);
        
        // Send welcome email
        try {
            userService.sendWelcomeEmail(registrationDto.getEmail(), registrationDto.getFirstName());
        } catch (Exception e) {
            log.warn("Failed to send welcome email: {}", e.getMessage());
        }
        
        return userProfile;
    }
    
    public void verifyEmail(String token) {
        log.info("Verifying email with token");
        userService.verifyEmail(token);
    }
    
    public void requestPasswordReset(String email) {
        log.info("Requesting password reset for email: {}", email);
        userService.requestPasswordReset(email);
    }
    
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token");
        userService.resetPassword(token, newPassword);
    }
    
    public UserProfileDto getCurrentUserProfile(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .accountStatus(user.getAccountStatus().name())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
