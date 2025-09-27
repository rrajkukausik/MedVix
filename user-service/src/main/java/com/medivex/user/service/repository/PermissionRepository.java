package com.medivex.user.service.repository;

import com.medivex.user.service.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true")
    List<Permission> findAllActive();
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true")
    Page<Permission> findAllActive(Pageable pageable);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.resource) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.action) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Permission> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND p.resource = :resource")
    List<Permission> findByResource(@Param("resource") String resource);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND p.action = :action")
    List<Permission> findByAction(@Param("action") String action);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND p.resource = :resource AND p.action = :action")
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);
    
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT p.resource FROM Permission p WHERE p.isActive = true ORDER BY p.resource")
    List<String> findAllDistinctResources();
    
    @Query("SELECT DISTINCT p.action FROM Permission p WHERE p.isActive = true ORDER BY p.action")
    List<String> findAllDistinctActions();
}
