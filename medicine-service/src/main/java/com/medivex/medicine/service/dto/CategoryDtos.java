package com.medivex.medicine.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class CategoryDtos {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryCreateRequest {
        @NotBlank @Size(min = 3, max = 100)
        private String name;
        @Size(max = 500)
        private String description;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CategoryUpdateRequest {
        @Size(min = 3, max = 100)
        private String name;
        @Size(max = 500)
        private String description;
    }
}
