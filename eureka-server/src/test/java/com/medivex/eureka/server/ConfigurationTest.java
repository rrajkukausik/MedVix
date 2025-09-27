package com.medivex.eureka.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class ConfigurationTest {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${eureka.instance.hostname}")
    private String eurekaHostname;

    @Value("${eureka.client.register-with-eureka}")
    private boolean registerWithEureka;

    @Value("${eureka.client.fetch-registry}")
    private boolean fetchRegistry;

    @Value("${eureka.client.service-url.defaultZone}")
    private String defaultZone;

    @Value("${eureka.server.wait-time-in-ms-when-sync-empty}")
    private int waitTimeInMsWhenSyncEmpty;

    @Value("${eureka.server.eviction-interval-timer-in-ms}")
    private int evictionIntervalTimerInMs;

    @Value("${management.endpoints.web.exposure.include}")
    private String managementEndpointsInclude;

    @Value("${management.endpoint.health.show-details}")
    private String healthShowDetails;

    @Test
    void applicationName_ShouldBeEurekaServer() {
        assertThat(applicationName).isEqualTo("eureka-server");
    }

    @Test
    void serverPort_ShouldBe8761() {
        assertThat(serverPort).isEqualTo(8761);
    }

    @Test
    void eurekaHostname_ShouldBeLocalhost() {
        assertThat(eurekaHostname).isEqualTo("localhost");
    }

    @Test
    void registerWithEureka_ShouldBeFalse() {
        assertThat(registerWithEureka).isFalse();
    }

    @Test
    void fetchRegistry_ShouldBeFalse() {
        assertThat(fetchRegistry).isFalse();
    }

    @Test
    void defaultZone_ShouldBeCorrect() {
        assertThat(defaultZone).isEqualTo("http://localhost:8761/eureka/");
    }

    @Test
    void waitTimeInMsWhenSyncEmpty_ShouldBe5000() {
        assertThat(waitTimeInMsWhenSyncEmpty).isEqualTo(5000);
    }

    @Test
    void evictionIntervalTimerInMs_ShouldBe5000() {
        assertThat(evictionIntervalTimerInMs).isEqualTo(5000);
    }

    @Test
    void managementEndpointsInclude_ShouldContainHealthInfoMetrics() {
        assertThat(managementEndpointsInclude).contains("health");
        assertThat(managementEndpointsInclude).contains("info");
        assertThat(managementEndpointsInclude).contains("metrics");
    }

    @Test
    void healthShowDetails_ShouldBeAlways() {
        assertThat(healthShowDetails).isEqualTo("always");
    }

    @Test
    void configurationProperties_ShouldBeLoaded() {
        // Test that all configuration properties are loaded correctly
        assertThat(applicationName).isNotNull();
        assertThat(serverPort).isPositive();
        assertThat(eurekaHostname).isNotNull();
        assertThat(defaultZone).isNotNull();
        assertThat(managementEndpointsInclude).isNotNull();
        assertThat(healthShowDetails).isNotNull();
    }

    @Test
    void eurekaConfiguration_ShouldBeValid() {
        // Test that Eureka configuration is valid
        assertThat(registerWithEureka).isFalse();
        assertThat(fetchRegistry).isFalse();
        assertThat(defaultZone).startsWith("http://");
        assertThat(defaultZone).endsWith("/eureka/");
        assertThat(waitTimeInMsWhenSyncEmpty).isPositive();
        assertThat(evictionIntervalTimerInMs).isPositive();
    }

    @Test
    void managementConfiguration_ShouldBeValid() {
        // Test that management configuration is valid
        assertThat(managementEndpointsInclude).isNotEmpty();
        assertThat(healthShowDetails).isEqualTo("always");
    }
}
