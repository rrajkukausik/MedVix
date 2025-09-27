package com.medivex.user.service.controller;

import com.medivex.user.service.dto.*;
import com.medivex.user.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        UserProfileDto userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileDto updateDto) {
        Long userId = getCurrentUserId(authentication);
        UserProfileDto userProfile = userService.updateUserProfile(userId, updateDto);
        return ResponseEntity.ok(userProfile);
    }
    
    @PostMapping("/me/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        Long userId = getCurrentUserId(authentication);
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(Pageable pageable) {
        Page<UserProfileDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileDto>> searchUsers(
            @RequestParam String search,
            Pageable pageable) {
        Page<UserProfileDto> users = userService.searchUsers(search, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable Long id) {
        UserProfileDto userProfile = userService.getUserById(id);
        return ResponseEntity.ok(userProfile);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileDto updateDto) {
        UserProfileDto userProfile = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(userProfile);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }
    
    @PostMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> assignRole(
            @PathVariable Long id,
            @PathVariable Long roleId) {
        userService.assignRole(id, roleId);
        return ResponseEntity.ok(Map.of("message", "Role assigned successfully"));
    }
    
    @DeleteMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removeRole(
            @PathVariable Long id,
            @PathVariable Long roleId) {
        userService.removeRole(id, roleId);
        return ResponseEntity.ok(Map.of("message", "Role removed successfully"));
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        // This is a simplified approach - in a real application, you might want to store user ID in JWT claims
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
