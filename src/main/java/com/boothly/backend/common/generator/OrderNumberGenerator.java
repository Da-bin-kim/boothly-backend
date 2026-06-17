package com.boothly.backend.common.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderNumberGenerator {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    public String generate() {
        String date = LocalDate.now(KST).format(DATE_FORMATTER);
        String timeFactor = String.format("%04d", System.currentTimeMillis() % 10000);
        String randomFactor = String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
        return "BO" + date + timeFactor + randomFactor;
    }
}