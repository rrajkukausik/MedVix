package com.medivex.medicine.service.service;

import com.medivex.medicine.service.dto.MedicineDtos.MedicineCreateRequest;
import com.medivex.medicine.service.dto.MedicineDtos.MedicineResponse;
import com.medivex.medicine.service.dto.MedicineDtos.MedicineUpdateRequest;
import com.medivex.medicine.service.entity.Medicine;
import com.medivex.medicine.service.entity.MedicineCategory;
import com.medivex.medicine.service.repository.MedicineCategoryRepository;
import com.medivex.medicine.service.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineCategoryRepository categoryRepository;

    public Page<MedicineResponse> list(Pageable pageable) {
        return medicineRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<MedicineResponse> listActive(Pageable pageable) {
        return medicineRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public MedicineResponse get(Long id) {
        Medicine m = medicineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Medicine not found"));
        return toResponse(m);
    }

    public MedicineResponse create(MedicineCreateRequest req, String username) {
        if (req.getMedicineCode() != null && medicineRepository.findByMedicineCode(req.getMedicineCode()).isPresent()) {
            throw new IllegalArgumentException("Medicine code already exists");
        }
        if (req.getBarcode() != null && !req.getBarcode().isBlank() && medicineRepository.findByBarcode(req.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Barcode already exists");
        }
        MedicineCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        Medicine m = new Medicine();
        m.setName(req.getName());
        m.setGenericName(req.getGenericName());
        m.setBrandName(req.getBrandName());
        m.setManufacturer(req.getManufacturer());
        m.setCategory(category);
        m.setMedicineCode(req.getMedicineCode());
        m.setBarcode(req.getBarcode());
        m.setDosageForm(req.getDosageForm());
        m.setStrength(req.getStrength());
        m.setUnitOfMeasure(req.getUnitOfMeasure());
        m.setPrescriptionRequired(Boolean.TRUE.equals(req.getPrescriptionRequired()));
        m.setMinimumStockLevel(req.getMinimumStockLevel());
        m.setMaximumStockLevel(req.getMaximumStockLevel());
        m.setActive(req.getActive() == null ? true : req.getActive());
        m.setCreatedBy(username);
        Medicine saved = medicineRepository.save(m);
        return toResponse(saved);
    }

    public MedicineResponse update(Long id, MedicineUpdateRequest req, String username) {
        Medicine m = medicineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Medicine not found"));
        if (req.getName() != null) m.setName(req.getName());
        if (req.getGenericName() != null) m.setGenericName(req.getGenericName());
        if (req.getBrandName() != null) m.setBrandName(req.getBrandName());
        if (req.getManufacturer() != null) m.setManufacturer(req.getManufacturer());
        if (req.getCategoryId() != null) {
            MedicineCategory category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            m.setCategory(category);
        }
        if (req.getMedicineCode() != null) {
            if (medicineRepository.findByMedicineCode(req.getMedicineCode()).filter(x -> !x.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Medicine code already exists");
            }
            m.setMedicineCode(req.getMedicineCode());
        }
        if (req.getBarcode() != null) {
            if (!req.getBarcode().isBlank() && medicineRepository.findByBarcode(req.getBarcode()).filter(x -> !x.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Barcode already exists");
            }
            m.setBarcode(req.getBarcode());
        }
        if (req.getDosageForm() != null) m.setDosageForm(req.getDosageForm());
        if (req.getStrength() != null) m.setStrength(req.getStrength());
        if (req.getUnitOfMeasure() != null) m.setUnitOfMeasure(req.getUnitOfMeasure());
        if (req.getPrescriptionRequired() != null) m.setPrescriptionRequired(req.getPrescriptionRequired());
        if (req.getMinimumStockLevel() != null) m.setMinimumStockLevel(req.getMinimumStockLevel());
        if (req.getMaximumStockLevel() != null) m.setMaximumStockLevel(req.getMaximumStockLevel());
        if (req.getActive() != null) m.setActive(req.getActive());
        m.setUpdatedBy(username);
        Medicine saved = medicineRepository.save(m);
        return toResponse(saved);
    }

    public void softDelete(Long id, String username) {
        Medicine m = medicineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Medicine not found"));
        m.setActive(false);
        m.setUpdatedBy(username);
        medicineRepository.save(m);
    }

    public Page<MedicineResponse> medicinesByCategory(Long categoryId, Pageable pageable) {
        MedicineCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        return medicineRepository.findByCategory(category, pageable).map(this::toResponse);
    }

    public Page<MedicineResponse> search(String q, Long categoryId, Boolean prescriptionRequired, Boolean active, Pageable pageable) {
        return medicineRepository.search(q, categoryId, prescriptionRequired, active, pageable).map(this::toResponse);
    }

    private MedicineResponse toResponse(Medicine m) {
        return MedicineResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .genericName(m.getGenericName())
                .brandName(m.getBrandName())
                .manufacturer(m.getManufacturer())
                .categoryId(m.getCategory() != null ? m.getCategory().getId() : null)
                .categoryName(m.getCategory() != null ? m.getCategory().getName() : null)
                .medicineCode(m.getMedicineCode())
                .barcode(m.getBarcode())
                .dosageForm(m.getDosageForm())
                .strength(m.getStrength())
                .unitOfMeasure(m.getUnitOfMeasure())
                .prescriptionRequired(Boolean.TRUE.equals(m.getPrescriptionRequired()))
                .minimumStockLevel(m.getMinimumStockLevel())
                .maximumStockLevel(m.getMaximumStockLevel())
                .active(Boolean.TRUE.equals(m.getActive()))
                .build();
    }
}
