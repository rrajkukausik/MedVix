package com.medivex.eureka.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class EurekaServerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // with all required beans and configurations
        assertThat(true).isTrue();
    }

    @Test
    void healthEndpoint_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/health";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("service")).isEqualTo("Eureka Server");
        assertThat(response.getBody().get("port")).isEqualTo(8761);
    }

    @Test
    void infoEndpoint_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/info";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("MedVix Eureka Server is running!");
        assertThat(response.getBody().get("dashboard")).isEqualTo("http://localhost:8761");
        assertThat(response.getBody().get("health")).isEqualTo("http://localhost:8761/health");
        assertThat(response.getBody().get("eureka-dashboard")).isEqualTo("http://localhost:8761/");
    }

    @Test
    void eurekaDashboard_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void actuatorHealth_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void actuatorInfo_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/actuator/info";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void actuatorMetrics_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/actuator/metrics";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void eurekaServer_ShouldBeRunning() {
        // Test that the Eureka server is running by checking if we can access the dashboard
        String url = "http://localhost:" + port + "/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // The Eureka dashboard should contain some Eureka-specific content
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void applicationProperties_ShouldBeLoaded() {
        // Test that application properties are loaded correctly in the running context
        String healthUrl = "http://localhost:" + port + "/health";
        ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("port")).isEqualTo(8761);
    }

    @Test
    void springBootApplication_ShouldStartSuccessfully() {
        // This test verifies that the Spring Boot application starts successfully
        // with all required components
        assertThat(restTemplate).isNotNull();
        assertThat(port).isPositive();
    }
}
