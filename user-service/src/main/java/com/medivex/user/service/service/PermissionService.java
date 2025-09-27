package com.medivex.user.service.service;

import com.medivex.user.service.entity.Permission;
import com.medivex.user.service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }
    
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }
    
    public List<Permission> findAllActive() {
        return permissionRepository.findAllActive();
    }
    
    public Page<Permission> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAllActive(pageable);
    }
    
    public Page<Permission> searchPermissions(String search, Pageable pageable) {
        return permissionRepository.findBySearchTerm(search, pageable);
    }
    
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }
    
    public Permission createPermission(Permission permission) {
        log.info("Creating new permission: {}", permission.getName());
        
        if (permissionRepository.existsByName(permission.getName())) {
            throw new RuntimeException("Permission name already exists!");
        }
        
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully: {}", savedPermission.getName());
        
        return savedPermission;
    }
    
    public Permission updatePermission(Long id, Permission permission) {
        log.info("Updating permission: {}", id);
        
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        if (permission.getName() != null && !permission.getName().equals(existingPermission.getName())) {
            if (permissionRepository.existsByName(permission.getName())) {
                throw new RuntimeException("Permission name already exists!");
            }
            existingPermission.setName(permission.getName());
        }
        
        if (permission.getDescription() != null) {
            existingPermission.setDescription(permission.getDescription());
        }
        if (permission.getResource() != null) {
            existingPermission.setResource(permission.getResource());
        }
        if (permission.getAction() != null) {
            existingPermission.setAction(permission.getAction());
        }
        if (permission.getIsActive() != null) {
            existingPermission.setIsActive(permission.getIsActive());
        }
        
        Permission savedPermission = permissionRepository.save(existingPermission);
        log.info("Permission updated successfully: {}", savedPermission.getName());
        
        return savedPermission;
    }
    
    public void deletePermission(Long id) {
        log.info("Deleting permission: {}", id);
        
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        permission.setIsActive(false);
        permissionRepository.save(permission);
        
        log.info("Permission deleted successfully: {}", permission.getName());
    }
    
    public List<String> getAllResources() {
        return permissionRepository.findAllDistinctResources();
    }
    
    public List<String> getAllActions() {
        return permissionRepository.findAllDistinctActions();
    }
    
    public void initializeDefaultPermissions() {
        log.info("Initializing default permissions");
        
        // User management permissions
        createPermissionIfNotExists("USER_CREATE", "Create users", "USER", "CREATE");
        createPermissionIfNotExists("USER_READ", "Read users", "USER", "READ");
        createPermissionIfNotExists("USER_UPDATE", "Update users", "USER", "UPDATE");
        createPermissionIfNotExists("USER_DELETE", "Delete users", "USER", "DELETE");
        
        // Role management permissions
        createPermissionIfNotExists("ROLE_CREATE", "Create roles", "ROLE", "CREATE");
        createPermissionIfNotExists("ROLE_READ", "Read roles", "ROLE", "READ");
        createPermissionIfNotExists("ROLE_UPDATE", "Update roles", "ROLE", "UPDATE");
        createPermissionIfNotExists("ROLE_DELETE", "Delete roles", "ROLE", "DELETE");
        
        // Medicine management permissions
        createPermissionIfNotExists("MEDICINE_CREATE", "Create medicines", "MEDICINE", "CREATE");
        createPermissionIfNotExists("MEDICINE_READ", "Read medicines", "MEDICINE", "READ");
        createPermissionIfNotExists("MEDICINE_UPDATE", "Update medicines", "MEDICINE", "UPDATE");
        createPermissionIfNotExists("MEDICINE_DELETE", "Delete medicines", "MEDICINE", "DELETE");
        
        // Inventory management permissions
        createPermissionIfNotExists("INVENTORY_CREATE", "Create inventory", "INVENTORY", "CREATE");
        createPermissionIfNotExists("INVENTORY_READ", "Read inventory", "INVENTORY", "READ");
        createPermissionIfNotExists("INVENTORY_UPDATE", "Update inventory", "INVENTORY", "UPDATE");
        createPermissionIfNotExists("INVENTORY_DELETE", "Delete inventory", "INVENTORY", "DELETE");
        
        // Sales management permissions
        createPermissionIfNotExists("SALES_CREATE", "Create sales", "SALES", "CREATE");
        createPermissionIfNotExists("SALES_READ", "Read sales", "SALES", "READ");
        createPermissionIfNotExists("SALES_UPDATE", "Update sales", "SALES", "UPDATE");
        createPermissionIfNotExists("SALES_DELETE", "Delete sales", "SALES", "DELETE");
        
        // Customer management permissions
        createPermissionIfNotExists("CUSTOMER_CREATE", "Create customers", "CUSTOMER", "CREATE");
        createPermissionIfNotExists("CUSTOMER_READ", "Read customers", "CUSTOMER", "READ");
        createPermissionIfNotExists("CUSTOMER_UPDATE", "Update customers", "CUSTOMER", "UPDATE");
        createPermissionIfNotExists("CUSTOMER_DELETE", "Delete customers", "CUSTOMER", "DELETE");
        
        // Dealer management permissions
        createPermissionIfNotExists("DEALER_CREATE", "Create dealers", "DEALER", "CREATE");
        createPermissionIfNotExists("DEALER_READ", "Read dealers", "DEALER", "READ");
        createPermissionIfNotExists("DEALER_UPDATE", "Update dealers", "DEALER", "UPDATE");
        createPermissionIfNotExists("DEALER_DELETE", "Delete dealers", "DEALER", "DELETE");
        
        // Notification permissions
        createPermissionIfNotExists("NOTIFICATION_CREATE", "Create notifications", "NOTIFICATION", "CREATE");
        createPermissionIfNotExists("NOTIFICATION_READ", "Read notifications", "NOTIFICATION", "READ");
        createPermissionIfNotExists("NOTIFICATION_UPDATE", "Update notifications", "NOTIFICATION", "UPDATE");
        createPermissionIfNotExists("NOTIFICATION_DELETE", "Delete notifications", "NOTIFICATION", "DELETE");
        
        // Report permissions
        createPermissionIfNotExists("REPORT_READ", "Read reports", "REPORT", "READ");
        createPermissionIfNotExists("REPORT_EXPORT", "Export reports", "REPORT", "EXPORT");
        
        // System permissions
        createPermissionIfNotExists("SYSTEM_ADMIN", "System administration", "SYSTEM", "ADMIN");
        createPermissionIfNotExists("SYSTEM_CONFIG", "System configuration", "SYSTEM", "CONFIG");
    }
    
    private void createPermissionIfNotExists(String name, String description, String resource, String action) {
        if (!permissionRepository.existsByName(name)) {
            Permission permission = Permission.builder()
                    .name(name)
                    .description(description)
                    .resource(resource)
                    .action(action)
                    .isActive(true)
                    .build();
            permissionRepository.save(permission);
            log.info("Created permission: {}", name);
        }
    }
}
