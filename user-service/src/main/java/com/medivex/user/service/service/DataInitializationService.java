package com.medivex.user.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        try {
            // Initialize permissions first
            permissionService.initializeDefaultPermissions();
            log.info("Default permissions initialized");
            
            // Initialize roles
            roleService.initializeDefaultRoles();
            log.info("Default roles initialized");
            
            // Initialize default users for all roles
            userService.initializeDefaultUsers();
            log.info("Default users initialized");
            
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
        }
    }
}

