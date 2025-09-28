package com.medivex.medicine.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines", indexes = {
        @Index(name = "idx_medicine_name", columnList = "name"),
        @Index(name = "idx_medicine_generic", columnList = "generic_name"),
        @Index(name = "idx_medicine_brand", columnList = "brand_name"),
        @Index(name = "idx_medicine_code", columnList = "medicine_code", unique = true),
        @Index(name = "idx_medicine_barcode", columnList = "barcode", unique = true),
        @Index(name = "idx_medicine_price", columnList = "price")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "generic_name", length = 150)
    private String genericName;

    @Column(name = "brand_name", length = 150)
    private String brandName;

    @Column(length = 150)
    private String manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MedicineCategory category;

    @Column(name = "medicine_code", length = 100, unique = true)
    private String medicineCode;

    @Column(length = 100, unique = true)
    private String barcode;

    @Column(name = "dosage_form", length = 100)
    private String dosageForm;

    @Column(length = 100)
    private String strength;

    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "prescription_required")
    private Boolean prescriptionRequired = false;

    @Column(name = "min_stock_level")
    private Integer minimumStockLevel;

    @Column(name = "max_stock_level")
    private Integer maximumStockLevel;

    @Column(name = "is_active")
    private Boolean active = true;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
