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
            
            // Create default admin user if it doesn't exist
            createDefaultAdminUser();
            
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
        }
    }
    
    private void createDefaultAdminUser() {
        try {
            if (!userService.findByUsername("admin").isPresent()) {
                log.info("Creating default admin user");
                
                // This would need to be implemented in UserService
                // For now, we'll just log that it should be created
                log.info("Default admin user should be created manually with username: admin, password: admin123");
            }
        } catch (Exception e) {
            log.warn("Could not check/create default admin user: {}", e.getMessage());
        }
    }
}
