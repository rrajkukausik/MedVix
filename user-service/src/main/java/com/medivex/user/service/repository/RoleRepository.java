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
    
    List<Role> findAllByIsActiveTrue();
    
    Page<Role> findAllByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Role> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    Page<Role> findByRoleTypeAndIsActiveTrue(Role.RoleType roleType, Pageable pageable);
    
    Page<Role> findByIsSystemRoleAndIsActiveTrue(Boolean isSystemRole, Pageable pageable);
    
    boolean existsByName(String name);
    
    List<Role> findAllByIsSystemRoleTrueAndIsActiveTrue();
}

