package com.medivex.medicine.service.repository;

import com.medivex.medicine.service.entity.Medicine;
import com.medivex.medicine.service.entity.MedicineCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByMedicineCode(String medicineCode);

    Optional<Medicine> findByBarcode(String barcode);

    Page<Medicine> findByActiveTrue(Pageable pageable);

    Page<Medicine> findByCategory(MedicineCategory category, Pageable pageable);

    @Query("select m from Medicine m where " +
            "(:q is null or lower(m.name) like lower(concat('%',:q,'%')) " +
            "or lower(m.genericName) like lower(concat('%',:q,'%')) " +
            "or lower(m.brandName) like lower(concat('%',:q,'%'))) and " +
            "(:categoryId is null or m.category.id = :categoryId) and " +
            "(:prescriptionRequired is null or m.prescriptionRequired = :prescriptionRequired) and " +
            "(:active is null or m.active = :active)")
    Page<Medicine> search(String q, Long categoryId, Boolean prescriptionRequired, Boolean active, Pageable pageable);
}
