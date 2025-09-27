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
    
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    
    Page<User> findAllByDeletedAtIsNullAndAccountStatus(User.AccountStatus status, Pageable pageable);
    
    Page<User> findAllByDeletedAtIsNullAndEmailVerified(Boolean verified, Pageable pageable);
    
    Page<User> findAllByDeletedAtIsNullAndCreatedAtBetween(LocalDateTime startDate, 
                                     LocalDateTime endDate, 
                                     Pageable pageable);
    
    long countByDeletedAtIsNull();
    
    long countByDeletedAtIsNullAndAccountStatus(User.AccountStatus status);
    
    long countByDeletedAtIsNullAndEmailVerified(Boolean verified);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findAllByDeletedAtIsNullAndAccountLockedUntilIsNotNullAndAccountLockedUntilAfter(LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND (" +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))" +
           ")")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
}

