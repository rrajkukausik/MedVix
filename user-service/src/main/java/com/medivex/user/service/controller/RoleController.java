package com.medivex.user.service.controller;

import com.medivex.user.service.dto.RoleDto;
import com.medivex.user.service.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RoleDto>> getAllRoles(Pageable pageable) {
        Page<RoleDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RoleDto>> searchRoles(
            @RequestParam String search,
            Pageable pageable) {
        Page<RoleDto> roles = roleService.searchRoles(search, pageable);
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleDto createdRole = roleService.createRole(roleDto);
        return ResponseEntity.ok(createdRole);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDto roleDto) {
        RoleDto updatedRole = roleService.updateRole(id, roleDto);
        return ResponseEntity.ok(updatedRole);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
    }
    
    @PostMapping("/{id}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> assignPermission(
            @PathVariable Long id,
            @PathVariable Long permissionId) {
        roleService.assignPermission(id, permissionId);
        return ResponseEntity.ok(Map.of("message", "Permission assigned successfully"));
    }
    
    @DeleteMapping("/{id}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removePermission(
            @PathVariable Long id,
            @PathVariable Long permissionId) {
        roleService.removePermission(id, permissionId);
        return ResponseEntity.ok(Map.of("message", "Permission removed successfully"));
    }
}
