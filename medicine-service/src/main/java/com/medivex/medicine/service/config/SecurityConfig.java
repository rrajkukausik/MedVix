package com.medivex.medicine.service.config;

import com.medivex.medicine.service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // actuator and health open for tooling
                .requestMatchers("/actuator/**", "/api/health").permitAll()
                // GET endpoints authenticated
                .requestMatchers(HttpMethod.GET, 
                        "/api/medicines", 
                        "/api/medicines/active", 
                        "/api/medicines/search", 
                        "/api/medicines/categories", 
                        "/api/medicines/categories/*/medicines", 
                        "/api/medicines/*").authenticated()
                // Write endpoints require roles
                .requestMatchers(HttpMethod.POST, 
                        "/api/medicines", 
                        "/api/medicines/categories").hasAnyRole("ADMIN", "PHARMACIST")
                .requestMatchers(HttpMethod.PUT, 
                        "/api/medicines/*", 
                        "/api/medicines/categories/*").hasAnyRole("ADMIN", "PHARMACIST")
                .requestMatchers(HttpMethod.DELETE, 
                        "/api/medicines/*", 
                        "/api/medicines/categories/*").hasAnyRole("ADMIN", "PHARMACIST")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
