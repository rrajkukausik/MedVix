package com.medivex.medicine.service.repository;

import com.medivex.medicine.service.entity.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, Long> {
    Optional<MedicineCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
