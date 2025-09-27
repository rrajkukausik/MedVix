package com.medivex.eureka.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Eureka Server");
        health.put("timestamp", LocalDateTime.now());
        health.put("port", 8761);
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "MedVix Eureka Server is running!");
        info.put("dashboard", "http://localhost:8761");
        info.put("health", "http://localhost:8761/health");
        info.put("eureka-dashboard", "http://localhost:8761/");
        return ResponseEntity.ok(info);
    }
} 