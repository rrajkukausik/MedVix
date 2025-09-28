package com.medivex.medicine.service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

public class MedicineDtos {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MedicineResponse {
        private Long id;
        private String name;
        private String genericName;
        private String brandName;
        private String manufacturer;
        private Long categoryId;
        private String categoryName;
        private String medicineCode;
        private String barcode;
        private String dosageForm;
        private String strength;
        private String unitOfMeasure;
        private Boolean prescriptionRequired;
        private Integer minimumStockLevel;
        private Integer maximumStockLevel;
        private Boolean active;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MedicineCreateRequest {
        @NotBlank @Size(min = 3, max = 150)
        private String name;
        @Size(max = 150)
        private String genericName;
        @Size(max = 150)
        private String brandName;
        @Size(max = 150)
        private String manufacturer;
        @NotNull
        private Long categoryId;
        @NotBlank @Size(max = 100)
        private String medicineCode;
        @Size(max = 100)
        private String barcode;
        @Size(max = 100)
        private String dosageForm;
        @Size(max = 100)
        private String strength;
        @Size(max = 50)
        private String unitOfMeasure;
        @NotNull
        private Boolean prescriptionRequired;
        @Min(0)
        private Integer minimumStockLevel;
        @Min(0)
        private Integer maximumStockLevel;
        private Boolean active;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MedicineUpdateRequest {
        @Size(min = 3, max = 150)
        private String name;
        @Size(max = 150)
        private String genericName;
        @Size(max = 150)
        private String brandName;
        @Size(max = 150)
        private String manufacturer;
        private Long categoryId;
        @Size(max = 100)
        private String medicineCode;
        @Size(max = 100)
        private String barcode;
        @Size(max = 100)
        private String dosageForm;
        @Size(max = 100)
        private String strength;
        @Size(max = 50)
        private String unitOfMeasure;
        private Boolean prescriptionRequired;
        @Min(0)
        private Integer minimumStockLevel;
        @Min(0)
        private Integer maximumStockLevel;
        private Boolean active;
    }
}
