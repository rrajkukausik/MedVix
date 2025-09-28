package com.medivex.medicine.service.controller;

import com.medivex.medicine.service.dto.MedicineDtos.*;
import com.medivex.medicine.service.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Medicines")
@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @Operation(summary = "List medicines (paginated)")
    @GetMapping
    public ResponseEntity<Page<MedicineResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(medicineService.list(pageable));
    }

    @Operation(summary = "List active medicines (paginated)")
    @GetMapping("/active")
    public ResponseEntity<Page<MedicineResponse>> listActive(Pageable pageable) {
        return ResponseEntity.ok(medicineService.listActive(pageable));
    }

    @Operation(summary = "Get medicine by id")
    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.get(id));
    }

    @Operation(summary = "Create a new medicine")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<MedicineResponse> create(@Valid @RequestBody MedicineCreateRequest request, Authentication auth) {
        String username = auth != null ? auth.getName() : "system";
        return ResponseEntity.ok(medicineService.create(request, username));
    }

    @Operation(summary = "Update an existing medicine")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<MedicineResponse> update(@PathVariable Long id, @Valid @RequestBody MedicineUpdateRequest request, Authentication auth) {
        String username = auth != null ? auth.getName() : "system";
        return ResponseEntity.ok(medicineService.update(id, request, username));
    }

    @Operation(summary = "Soft delete (mark inactive) a medicine")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String username = auth != null ? auth.getName() : "system";
        medicineService.softDelete(id, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search medicines with filters")
    @GetMapping("/search")
    public ResponseEntity<Page<MedicineResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean prescriptionRequired,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        return ResponseEntity.ok(medicineService.search(q, categoryId, prescriptionRequired, active, pageable));
    }

    @Operation(summary = "Get medicines by category")
    @GetMapping("/categories/{id}/medicines")
    public ResponseEntity<Page<MedicineResponse>> byCategory(@PathVariable("id") Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(medicineService.medicinesByCategory(categoryId, pageable));
    }
}
