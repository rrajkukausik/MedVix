package com.medivex.medicine.service.service;

import com.medivex.medicine.service.entity.MedicineCategory;
import com.medivex.medicine.service.repository.MedicineCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineCategoryService {

    private final MedicineCategoryRepository categoryRepository;

    public Page<MedicineCategory> list(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public MedicineCategory create(MedicineCategory category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    public MedicineCategory update(Long id, MedicineCategory update) {
        MedicineCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        if (update.getName() != null && !update.getName().equalsIgnoreCase(existing.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(update.getName())) {
                throw new IllegalArgumentException("Category name already exists");
            }
            existing.setName(update.getName());
        }
        if (update.getDescription() != null) {
            existing.setDescription(update.getDescription());
        }
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    public MedicineCategory get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
    }
}
