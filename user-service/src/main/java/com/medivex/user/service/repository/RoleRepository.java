package com.medivex.user.service.repository;

import com.medivex.user.service.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    List<Role> findAllActive();
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    Page<Role> findAllActive(Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Role> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.roleType = :roleType")
    Page<Role> findByRoleType(@Param("roleType") Role.RoleType roleType, Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.isSystemRole = :isSystemRole")
    Page<Role> findBySystemRole(@Param("isSystemRole") Boolean isSystemRole, Pageable pageable);
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.isSystemRole = true")
    List<Role> findAllSystemRoles();
}
