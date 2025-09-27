package com.medivex.user.service.service;

import com.medivex.user.service.dto.RoleDto;
import com.medivex.user.service.entity.Permission;
import com.medivex.user.service.entity.Role;
import com.medivex.user.service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
    
    public List<Role> findAllActive() {
        return roleRepository.findAllActive();
    }
    
    public Page<RoleDto> getAllRoles(Pageable pageable) {
        Page<Role> roles = roleRepository.findAllActive(pageable);
        return roles.map(this::convertToRoleDto);
    }
    
    public Page<RoleDto> searchRoles(String search, Pageable pageable) {
        Page<Role> roles = roleRepository.findBySearchTerm(search, pageable);
        return roles.map(this::convertToRoleDto);
    }
    
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        return convertToRoleDto(role);
    }
    
    public RoleDto createRole(RoleDto roleDto) {
        log.info("Creating new role: {}", roleDto.getName());
        
        if (roleRepository.existsByName(roleDto.getName())) {
            throw new RuntimeException("Role name already exists!");
        }
        
        Role role = Role.builder()
                .name(roleDto.getName())
                .description(roleDto.getDescription())
                .roleType(Role.RoleType.CUSTOM)
                .isSystemRole(false)
                .isActive(true)
                .permissions(new HashSet<>())
                .build();
        
        // Add permissions if provided
        if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty()) {
            Set<Permission> permissions = roleDto.getPermissions().stream()
                    .map(permissionService::findByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());
        
        return convertToRoleDto(savedRole);
    }
    
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        log.info("Updating role: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        // Check if role name is being changed and if it's already taken
        if (roleDto.getName() != null && !roleDto.getName().equals(role.getName())) {
            if (roleRepository.existsByName(roleDto.getName())) {
                throw new RuntimeException("Role name already exists!");
            }
            role.setName(roleDto.getName());
        }
        
        if (roleDto.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }
        
        // Update permissions if provided
        if (roleDto.getPermissions() != null) {
            Set<Permission> permissions = roleDto.getPermissions().stream()
                    .map(permissionService::findByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role updated successfully: {}", savedRole.getName());
        
        return convertToRoleDto(savedRole);
    }
    
    public void deleteRole(Long id) {
        log.info("Deleting role: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        if (role.getIsSystemRole()) {
            throw new RuntimeException("Cannot delete system role!");
        }
        
        role.setIsActive(false);
        roleRepository.save(role);
        
        log.info("Role deleted successfully: {}", role.getName());
    }
    
    public void assignPermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        role.getPermissions().add(permission);
        roleRepository.save(role);
        
        log.info("Permission {} assigned to role {}", permission.getName(), role.getName());
    }
    
    public void removePermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        role.getPermissions().remove(permission);
        roleRepository.save(role);
        
        log.info("Permission {} removed from role {}", permission.getName(), role.getName());
    }
    
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");
        
        // Create ADMIN role
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Full access to all system features and configurations")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        }
        
        // Create PHARMACIST role
        if (!roleRepository.existsByName("PHARMACIST")) {
            Role pharmacistRole = Role.builder()
                    .name("PHARMACIST")
                    .description("Manages medicines, inventory, purchases, and sales")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(pharmacistRole);
            log.info("Created PHARMACIST role");
        }
        
        // Create CASHIER role
        if (!roleRepository.existsByName("CASHIER")) {
            Role cashierRole = Role.builder()
                    .name("CASHIER")
                    .description("Handles billing and customer interactions at the counter")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(cashierRole);
            log.info("Created CASHIER role");
        }
        
        // Create DEALER role
        if (!roleRepository.existsByName("DEALER")) {
            Role dealerRole = Role.builder()
                    .name("DEALER")
                    .description("Supplier role for order tracking")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(dealerRole);
            log.info("Created DEALER role");
        }
        
        // Create REPORT_VIEWER role
        if (!roleRepository.existsByName("REPORT_VIEWER")) {
            Role reportViewerRole = Role.builder()
                    .name("REPORT_VIEWER")
                    .description("View-only dashboard and report access")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(reportViewerRole);
            log.info("Created REPORT_VIEWER role");
        }
        
        // Create SUPPORT role
        if (!roleRepository.existsByName("SUPPORT")) {
            Role supportRole = Role.builder()
                    .name("SUPPORT")
                    .description("Manages notifications and customer support communications")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(supportRole);
            log.info("Created SUPPORT role");
        }
        
        // Create USER role
        if (!roleRepository.existsByName("USER")) {
            Role userRole = Role.builder()
                    .name("USER")
                    .description("Basic user role with limited access")
                    .roleType(Role.RoleType.SYSTEM)
                    .isSystemRole(true)
                    .isActive(true)
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(userRole);
            log.info("Created USER role");
        }
    }
    
    private RoleDto convertToRoleDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .roleType(role.getRoleType().name())
                .isSystemRole(role.getIsSystemRole())
                .isActive(role.getIsActive())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()))
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
