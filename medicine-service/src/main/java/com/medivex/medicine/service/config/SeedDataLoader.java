package com.medivex.medicine.service.config;

import com.medivex.medicine.service.entity.Medicine;
import com.medivex.medicine.service.entity.MedicineCategory;
import com.medivex.medicine.service.repository.MedicineCategoryRepository;
import com.medivex.medicine.service.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeedDataLoader {

    private final MedicineRepository medicineRepository;
    private final MedicineCategoryRepository categoryRepository;

    @Value("${medicine.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${medicine.seed.csvUrl:https://raw.githubusercontent.com/junioralive/Indian-Medicine-Dataset/refs/heads/main/DATA/indian_medicine_data.csv}")
    private String csvUrl;

    @Bean
    @Transactional
    CommandLineRunner seedMedicinesRunner() {
        return args -> {
            if (!seedEnabled) {
                log.info("Medicine seeding disabled by property.");
                return;
            }
            if (medicineRepository.count() > 0) {
                log.info("Medicines already present; skipping seed.");
                return;
            }
            try {
                log.info("Downloading CSV from {}", csvUrl);
                var url = URI.create(csvUrl).toURL();
                try (var stream = url.openStream();
                     var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                     CSVParser parser = CSVFormat.DEFAULT
                             .builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .build()
                             .parse(reader)) {

                    Map<String, MedicineCategory> categoryCache = new HashMap<>();
                    AtomicInteger created = new AtomicInteger();
                    AtomicInteger skipped = new AtomicInteger();

                    for (CSVRecord rec : parser) {
                        try {
                            String id = rec.get("id");
                            String name = rec.get("name");
                            String priceStr = rec.get("price(₹)");
                            String isDiscontinued = rec.get("Is_discontinued");
                            String manufacturer = rec.get("manufacturer_name");
                            String type = rec.get("type");
                            String packSize = rec.get("pack_size_label");
                            String comp1 = rec.get("short_composition1");
                            String comp2 = rec.get("short_composition2");

                            if (name == null || name.isBlank()) {
                                skipped.incrementAndGet();
                                continue;
                            }

                            // Category from type; fallback "Uncategorized"
                            String categoryName = (type == null || type.isBlank()) ? "Uncategorized" : capitalize(type.trim());
                            MedicineCategory category = categoryCache.computeIfAbsent(categoryName, n ->
                                    categoryRepository.findByNameIgnoreCase(n).orElseGet(() -> {
                                        MedicineCategory c = new MedicineCategory();
                                        c.setName(n);
                                        c.setDescription("Auto-created from CSV type field");
                                        return categoryRepository.save(c);
                                    })
                            );

                            Medicine m = new Medicine();
                            m.setName(name);
                            m.setBrandName(name); // treat csv name as brand name
                            m.setGenericName(extractGenericFromCompositions(comp1, comp2));
                            m.setManufacturer(manufacturer);
                            m.setCategory(category);
                            m.setMedicineCode("CSV-" + (id != null ? id.trim() : name.hashCode()));
                            m.setBarcode(null);
                            m.setDosageForm(extractDosageForm(packSize));
                            m.setStrength(extractStrength(name, comp1));
                            m.setUnitOfMeasure(extractUnitFromStrength(m.getStrength()));
                            m.setPrice(parsePrice(priceStr));
                            m.setPrescriptionRequired(Boolean.FALSE);
                            m.setMinimumStockLevel(0);
                            m.setMaximumStockLevel(0);
                            m.setActive(!Boolean.parseBoolean(Optional.ofNullable(isDiscontinued).orElse("false")));
                            m.setCreatedBy("seed");

                            // avoid duplicates by medicineCode
                            if (medicineRepository.findByMedicineCode(m.getMedicineCode()).isPresent()) {
                                skipped.incrementAndGet();
                                continue;
                            }
                            medicineRepository.save(m);
                            created.incrementAndGet();
                        } catch (Exception rowEx) {
                            log.warn("Skipping row due to parse error: {}", rowEx.getMessage());
                            skipped.incrementAndGet();
                        }
                    }
                    log.info("Medicine CSV seeding done. Created: {}, Skipped: {}", created.get(), skipped.get());
                }
            } catch (Exception e) {
                log.error("Failed to seed medicines from CSV", e);
            }
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static String extractDosageForm(String packSize) {
        if (packSize == null) return null;
        String p = packSize.toLowerCase();
        if (p.contains("tablet")) return "Tablet";
        if (p.contains("capsule")) return "Capsule";
        if (p.contains("syrup")) return "Syrup";
        if (p.contains("cream")) return "Cream";
        if (p.contains("gel")) return "Gel";
        if (p.contains("inhaler")) return "Inhaler";
        if (p.contains("solution")) return "Solution";
        if (p.contains("suspension")) return "Suspension";
        if (p.contains("drop")) return "Drops";
        if (p.contains("ointment")) return "Ointment";
        return null;
    }

    private static String extractGenericFromCompositions(String c1, String c2) {
        String base = (c1 != null && !c1.isBlank()) ? c1 : (c2 != null ? c2 : null);
        if (base == null) return null;
        // composition like "Amoxycillin  (500mg) ,  Clavulanic Acid (125mg)"
        String[] parts = base.split(",");
        if (parts.length > 0) {
            String first = parts[0].trim();
            // remove amount in parentheses
            return first.replaceAll("\\(.*?\\)", "").trim().replaceAll("\\s+", " ");
        }
        return null;
    }

    private static String extractStrength(String name, String c1) {
        // try from name like "Allegra 120mg Tablet"
        if (name != null && name.matches(".*\\b(\\d+\\.?\\d*)(mg|mcg|g|ml)\\b.*")) {
            return name.replaceAll(".*\\b(\\d+\\.?\\d*(?:mg|mcg|g|ml))\\b.*", "$1");
        }
        // fallback from composition1 first dose inside parentheses
        if (c1 != null && c1.matches(".*\\(.*?\\d+.*?\\).*")) {
            String inside = c1.replaceAll(".*\\((.*?)\\).*", "$1");
            // choose first token like 500mg
            var m = inside.split(",");
            if (m.length > 0) {
                String token = m[0].trim();
                // keep only dose number+unit
                return token.replaceAll(".*?(\\d+\\.?\\d*(?:mg|mcg|g|ml)).*", "$1");
            }
        }
        return null;
    }

    private static String extractUnitFromStrength(String strength) {
        if (strength == null) return null;
        if (strength.toLowerCase().contains("mg")) return "mg";
        if (strength.toLowerCase().contains("mcg")) return "mcg";
        if (strength.toLowerCase().contains("g")) return "g";
        if (strength.toLowerCase().contains("ml")) return "ml";
        return null;
    }

    private static BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return null;
        try {
            String normalized = priceStr.replaceAll("[₹,]", "").trim();
            if (normalized.isBlank()) return null;
            return new BigDecimal(normalized);
        } catch (Exception e) {
            return null;
        }
    }
}
