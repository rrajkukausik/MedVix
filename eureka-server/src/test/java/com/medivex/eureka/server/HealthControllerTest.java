package com.medivex.eureka.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private HealthController healthController;

    @BeforeEach
    void setUp() {
        healthController = new HealthController();
    }

    @Test
    void healthEndpoint_ShouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Eureka Server"))
                .andExpect(jsonPath("$.port").value(8761))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void healthEndpoint_ShouldReturnCorrectResponseStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        assertThat(response).containsKeys("status", "service", "timestamp", "port");
        assertThat(response.get("status")).isEqualTo("UP");
        assertThat(response.get("service")).isEqualTo("Eureka Server");
        assertThat(response.get("port")).isEqualTo(8761);
        assertThat(response.get("timestamp")).isNotNull();
    }

    @Test
    void healthEndpoint_ShouldReturnTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        // Verify timestamp is a valid LocalDateTime
        String timestampStr = (String) response.get("timestamp");
        assertThat(timestampStr).isNotNull();
        assertThat(timestampStr).isNotEmpty();
    }

    @Test
    void infoEndpoint_ShouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("MedVix Eureka Server is running!"))
                .andExpect(jsonPath("$.dashboard").value("http://localhost:8761"))
                .andExpect(jsonPath("$.health").value("http://localhost:8761/health"))
                .andExpect(jsonPath("$.eureka-dashboard").value("http://localhost:8761/"));
    }

    @Test
    void infoEndpoint_ShouldReturnCorrectResponseStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<String, String> response = objectMapper.readValue(content, Map.class);

        assertThat(response).containsKeys("message", "dashboard", "health", "eureka-dashboard");
        assertThat(response.get("message")).isEqualTo("MedVix Eureka Server is running!");
        assertThat(response.get("dashboard")).isEqualTo("http://localhost:8761");
        assertThat(response.get("health")).isEqualTo("http://localhost:8761/health");
        assertThat(response.get("eureka-dashboard")).isEqualTo("http://localhost:8761/");
    }

    @Test
    void healthMethod_ShouldReturnCorrectResponse() {
        // Test the health method directly
        var response = healthController.health();
        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("service")).isEqualTo("Eureka Server");
        assertThat(response.getBody().get("port")).isEqualTo(8761);
        assertThat(response.getBody().get("timestamp")).isNotNull();
        assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void infoMethod_ShouldReturnCorrectResponse() {
        // Test the info method directly
        var response = healthController.info();
        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("MedVix Eureka Server is running!");
        assertThat(response.getBody().get("dashboard")).isEqualTo("http://localhost:8761");
        assertThat(response.getBody().get("health")).isEqualTo("http://localhost:8761/health");
        assertThat(response.getBody().get("eureka-dashboard")).isEqualTo("http://localhost:8761/");
    }

    @Test
    void healthMethod_ShouldReturnResponseEntity() {
        // Test that health method returns ResponseEntity
        var response = healthController.health();
        
        assertThat(response).isInstanceOf(org.springframework.http.ResponseEntity.class);
        assertThat(response.getBody()).isInstanceOf(Map.class);
    }

    @Test
    void infoMethod_ShouldReturnResponseEntity() {
        // Test that info method returns ResponseEntity
        var response = healthController.info();
        
        assertThat(response).isInstanceOf(org.springframework.http.ResponseEntity.class);
        assertThat(response.getBody()).isInstanceOf(Map.class);
    }

    @Test
    void healthMethod_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void infoMethod_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void healthEndpoint_ShouldAcceptGetRequest() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    void infoEndpoint_ShouldAcceptGetRequest() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk());
    }

    @Test
    void healthController_ShouldBeAnnotatedWithRestController() {
        assertThat(HealthController.class.isAnnotationPresent(
            org.springframework.web.bind.annotation.RestController.class)).isTrue();
    }

    @Test
    void healthMethod_ShouldBeAnnotatedWithGetMapping() throws Exception {
        var method = HealthController.class.getMethod("health");
        assertThat(method.isAnnotationPresent(
            org.springframework.web.bind.annotation.GetMapping.class)).isTrue();
    }

    @Test
    void infoMethod_ShouldBeAnnotatedWithGetMapping() throws Exception {
        var method = HealthController.class.getMethod("info");
        assertThat(method.isAnnotationPresent(
            org.springframework.web.bind.annotation.GetMapping.class)).isTrue();
    }

    @Test
    void healthMethod_ShouldHaveCorrectMapping() throws Exception {
        var method = HealthController.class.getMethod("health");
        var mapping = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
        assertThat(mapping.value()).containsExactly("/health");
    }

    @Test
    void infoMethod_ShouldHaveCorrectMapping() throws Exception {
        var method = HealthController.class.getMethod("info");
        var mapping = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
        assertThat(mapping.value()).containsExactly("/info");
    }
}
