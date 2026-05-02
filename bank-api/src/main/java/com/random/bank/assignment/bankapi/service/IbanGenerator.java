package com.random.bank.assignment.bankapi.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class IbanGenerator {

    private static final String COUNTRY_CODE = "NL";
    private static final String BANK_CODE = "RBAN";

    private final SecureRandom random = new SecureRandom();

    public String generate() {

        String accountNumber = generateAccountNumber();

        String checkDigits = String.format("%02d", random.nextInt(100));

        return COUNTRY_CODE + checkDigits + BANK_CODE + accountNumber;
    }

    private String generateAccountNumber() {
        long millis = Instant.now().toEpochMilli();

        // take last 6 digits of timestamp (fast-changing)
        long timePart = millis % 1000000;

        // add 4-digit random to avoid same-millisecond collisions
        int randomPart = random.nextInt(10000);

        // Timestamp + random suffix keeps the number readable and reduces collision risk.
        return String.format("%06d%04d", timePart, randomPart);
    }
}
