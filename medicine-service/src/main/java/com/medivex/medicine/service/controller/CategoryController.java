package com.medivex.medicine.service.controller;

import com.medivex.medicine.service.dto.CategoryDtos.*;
import com.medivex.medicine.service.entity.MedicineCategory;
import com.medivex.medicine.service.service.MedicineCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Medicine Categories")
@RestController
@RequestMapping("/api/medicines/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final MedicineCategoryService categoryService;

    @Operation(summary = "List medicine categories (paginated)")
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(categoryService.list(pageable).map(this::toResponse));
    }

    @Operation(summary = "Create a new medicine category")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest req) {
        MedicineCategory cat = new MedicineCategory();
        cat.setName(req.getName());
        cat.setDescription(req.getDescription());
        return ResponseEntity.ok(toResponse(categoryService.create(cat)));
    }

    @Operation(summary = "Update a medicine category")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest req) {
        MedicineCategory update = new MedicineCategory();
        update.setName(req.getName());
        update.setDescription(req.getDescription());
        return ResponseEntity.ok(toResponse(categoryService.update(id, update)));
    }

    @Operation(summary = "Delete a medicine category")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(MedicineCategory c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}
