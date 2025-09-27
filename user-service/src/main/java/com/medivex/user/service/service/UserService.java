package com.medivex.user.service.service;

import com.medivex.user.service.dto.*;
import com.medivex.user.service.entity.Role;
import com.medivex.user.service.entity.User;
import com.medivex.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EmailService emailService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getName())
                        .toArray(String[]::new))
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }
    
    public UserProfileDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user: {}", registrationDto.getUsername());
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .phone(registrationDto.getPhone())
                .emailVerificationToken(UUID.randomUUID().toString())
                .accountStatus(User.AccountStatus.ACTIVE)
                .emailVerified(false)
                .failedLoginAttempts(0)
                .roles(new HashSet<>())
                .build();
        
        // Assign default role (USER)
        Role defaultRole = roleService.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        user.getRoles().add(defaultRole);
        
        User savedUser = userRepository.save(user);
        
        // Send email verification
        try {
            emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getEmailVerificationToken());
        } catch (Exception e) {
            log.warn("Failed to send email verification: {}", e.getMessage());
        }
        
        log.info("User registered successfully: {}", savedUser.getUsername());
        return convertToUserProfileDto(savedUser);
    }
    
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToUserProfileDto(user);
    }
    
    public UserProfileDto updateUserProfile(Long userId, UpdateUserProfileDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if email is being changed and if it's already taken
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(updateDto.getEmail());
            user.setEmailVerified(false);
            user.setEmailVerificationToken(UUID.randomUUID().toString());
        }
        
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        
        User savedUser = userRepository.save(user);
        return convertToUserProfileDto(savedUser);
    }
    
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Verify new password confirmation
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", user.getUsername());
    }
    
    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        user.setLastLogin(LocalDateTime.now());
        user.setFailedLoginAttempts(0); // Reset failed attempts on successful login
        userRepository.save(user);
    }
    
    public void handleFailedLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        
        if (user != null) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                log.warn("Account locked for user: {} due to too many failed login attempts", username);
            }
            
            userRepository.save(user);
        }
    }
    
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        
        log.info("Email verified successfully for user: {}", user.getUsername());
    }
    
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        userRepository.save(user);
        
        try {
            emailService.sendPasswordResetEmail(email, resetToken);
        } catch (Exception e) {
            log.warn("Failed to send password reset email: {}", e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String email, String firstName) {
        try {
            emailService.sendWelcomeEmail(email, firstName);
        } catch (Exception e) {
            log.warn("Failed to send welcome email: {}", e.getMessage());
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getUsername());
    }
    
    // Admin methods
    public Page<UserProfileDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAllActive(pageable);
        return users.map(this::convertToUserProfileDto);
    }
    
    public Page<UserProfileDto> searchUsers(String search, Pageable pageable) {
        Page<User> users = userRepository.findBySearchTerm(search, pageable);
        return users.map(this::convertToUserProfileDto);
    }
    
    public UserProfileDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToUserProfileDto(user);
    }
    
    public UserProfileDto updateUser(Long id, UpdateUserProfileDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(updateDto.getEmail());
        }
        
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        
        User savedUser = userRepository.save(user);
        return convertToUserProfileDto(savedUser);
    }
    
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setAccountStatus(User.AccountStatus.INACTIVE);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User deactivated: {}", user.getUsername());
    }
    
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        user.setDeletedAt(null);
        userRepository.save(user);
        
        log.info("User activated: {}", user.getUsername());
    }
    
    public void assignRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleService.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.getRoles().add(role);
        userRepository.save(user);
        
        log.info("Role {} assigned to user {}", role.getName(), user.getUsername());
    }
    
    public void removeRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleService.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.getRoles().remove(role);
        userRepository.save(user);
        
        log.info("Role {} removed from user {}", role.getName(), user.getUsername());
    }
    
    private UserProfileDto convertToUserProfileDto(User user) {
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
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
