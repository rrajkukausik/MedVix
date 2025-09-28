package com.medivex.medicine.service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Medicine Service API",
                version = "v1",
                description = "APIs for medicine catalog and categories"
        )
)
@Configuration
public class OpenApiConfig {
}
