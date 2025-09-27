package com.medivex.user.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    @Column(unique = true, nullable = false)
    private String name;
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    @NotBlank(message = "Resource is required")
    @Size(max = 50, message = "Resource must not exceed 50 characters")
    @Column(nullable = false)
    private String resource;
    
    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action must not exceed 50 characters")
    @Column(nullable = false)
    private String action;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
}
