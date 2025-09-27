package com.medivex.eureka.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class ApplicationPropertiesTest {

    @Test
    void applicationPropertiesFile_ShouldExist() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.properties");
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void applicationPropertiesFile_ShouldContainApplicationName() throws IOException {
        Properties properties = loadProperties("application.properties");
        assertThat(properties.getProperty("spring.application.name")).isEqualTo("eureka-server");
    }

    @Test
    void applicationYmlFile_ShouldExist() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.yml");
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void applicationYmlFile_ShouldBeReadable() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            assertThat(inputStream).isNotNull();
            assertThat(inputStream.available()).isGreaterThan(0);
        }
    }

    @Test
    void applicationProperties_ShouldHaveCorrectStructure() throws IOException {
        Properties properties = loadProperties("application.properties");
        
        // Verify that the properties file has the expected structure
        assertThat(properties).isNotNull();
        assertThat(properties.getProperty("spring.application.name")).isNotNull();
    }

    @Test
    void applicationYml_ShouldContainServerConfiguration() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes());
            
            // Verify that the YAML contains expected configuration
            assertThat(content).contains("server:");
            assertThat(content).contains("port: 8761");
            assertThat(content).contains("spring:");
            assertThat(content).contains("application:");
            assertThat(content).contains("name: eureka-server");
            assertThat(content).contains("eureka:");
            assertThat(content).contains("management:");
        }
    }

    @Test
    void applicationYml_ShouldContainEurekaConfiguration() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes());
            
            // Verify Eureka-specific configuration
            assertThat(content).contains("eureka:");
            assertThat(content).contains("instance:");
            assertThat(content).contains("hostname: localhost");
            assertThat(content).contains("client:");
            assertThat(content).contains("register-with-eureka: false");
            assertThat(content).contains("fetch-registry: false");
            assertThat(content).contains("server:");
        }
    }

    @Test
    void applicationYml_ShouldContainManagementConfiguration() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes());
            
            // Verify management configuration
            assertThat(content).contains("management:");
            assertThat(content).contains("endpoints:");
            assertThat(content).contains("web:");
            assertThat(content).contains("exposure:");
            assertThat(content).contains("include: health,info,metrics");
            assertThat(content).contains("endpoint:");
            assertThat(content).contains("health:");
            assertThat(content).contains("show-details: always");
        }
    }

    private Properties loadProperties(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        Properties properties = new Properties();
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
        }
        return properties;
    }
}
