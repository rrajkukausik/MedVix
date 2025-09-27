package com.medivex.user.service.repository;

import com.medivex.user.service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    Page<User> findAllActive(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.accountStatus = :status")
    Page<User> findByAccountStatus(@Param("status") User.AccountStatus status, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.emailVerified = :verified")
    Page<User> findByEmailVerified(@Param("verified") Boolean verified, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findByCreatedDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND u.accountStatus = :status")
    long countByAccountStatus(@Param("status") User.AccountStatus status);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL AND u.emailVerified = :verified")
    long countByEmailVerified(@Param("verified") Boolean verified);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :now")
    List<User> findLockedAccounts(@Param("now") LocalDateTime now);
}
