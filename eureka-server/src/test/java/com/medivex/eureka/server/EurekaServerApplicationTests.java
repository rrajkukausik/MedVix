package com.medivex.eureka.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(classes = EurekaServerApplication.class)
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // with all required beans and configurations
        assertTrue(true, "Application context should load successfully");
    }

    @Test
    void eurekaServerAnnotationPresent() {
        // Verify that the @EnableEurekaServer annotation is present
        assertTrue(EurekaServerApplication.class.isAnnotationPresent(
            org.springframework.cloud.netflix.eureka.server.EnableEurekaServer.class),
            "EurekaServerApplication should be annotated with @EnableEurekaServer");
    }

    @Test
    void springBootApplicationAnnotationPresent() {
        // Verify that the @SpringBootApplication annotation is present
        assertTrue(EurekaServerApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class),
            "EurekaServerApplication should be annotated with @SpringBootApplication");
    }

    @Test
    void mainMethodExists() {
        // Verify that the main method exists and is public static
        try {
            EurekaServerApplication.class.getMethod("main", String[].class);
            assertTrue(true, "Main method should exist");
        } catch (NoSuchMethodException e) {
            fail("Main method should exist");
        }
    }

    @Test
    void applicationClassIsPublic() {
        // Verify that the application class is public
        assertTrue(java.lang.reflect.Modifier.isPublic(EurekaServerApplication.class.getModifiers()),
            "EurekaServerApplication class should be public");
    }

    @Test
    void applicationClassIsNotAbstract() {
        // Verify that the application class is not abstract
        assertFalse(java.lang.reflect.Modifier.isAbstract(EurekaServerApplication.class.getModifiers()),
            "EurekaServerApplication class should not be abstract");
    }

    @Test
    void applicationClassIsNotFinal() {
        // Verify that the application class is not final
        assertFalse(java.lang.reflect.Modifier.isFinal(EurekaServerApplication.class.getModifiers()),
            "EurekaServerApplication class should not be final");
    }

    @Test
    void mainMethodSignature() {
        // Test that the main method has the correct signature
        try {
            var method = EurekaServerApplication.class.getMethod("main", String[].class);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()),
                "Main method should be static");
            assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()),
                "Main method should be public");
            assertThat(method.getReturnType()).isEqualTo(void.class);
            assertThat(method.getParameterTypes()).hasSize(1);
            assertThat(method.getParameterTypes()[0]).isEqualTo(String[].class);
        } catch (NoSuchMethodException e) {
            fail("Main method should exist");
        }
    }

    @Test
    void applicationClassAnnotations() {
        // Test that the application class has the correct annotations
        var annotations = EurekaServerApplication.class.getAnnotations();
        assertThat(annotations).hasSize(2);
        
        boolean hasSpringBootApplication = false;
        boolean hasEnableEurekaServer = false;
        
        for (var annotation : annotations) {
            if (annotation.annotationType().equals(org.springframework.boot.autoconfigure.SpringBootApplication.class)) {
                hasSpringBootApplication = true;
            }
            if (annotation.annotationType().equals(org.springframework.cloud.netflix.eureka.server.EnableEurekaServer.class)) {
                hasEnableEurekaServer = true;
            }
        }
        
        assertTrue(hasSpringBootApplication, "Should have @SpringBootApplication annotation");
        assertTrue(hasEnableEurekaServer, "Should have @EnableEurekaServer annotation");
    }

    @Test
    void applicationClassPackage() {
        // Test that the application class is in the correct package
        assertThat(EurekaServerApplication.class.getPackage().getName())
            .isEqualTo("com.medivex.eureka.server");
    }

    @Test
    void applicationClassSimpleName() {
        // Test that the application class has the correct simple name
        assertThat(EurekaServerApplication.class.getSimpleName())
            .isEqualTo("EurekaServerApplication");
    }

    @Test
    void mainMethod_ShouldExecuteSuccessfully() {
        // Test that the main method executes without throwing exceptions
        // This test will actually execute the main method to achieve 100% coverage
        assertDoesNotThrow(() -> {
            EurekaServerApplication.main(new String[]{"--server.port=0"});
        });
    }
}
