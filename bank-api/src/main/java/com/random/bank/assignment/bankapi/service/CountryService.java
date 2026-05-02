package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.entity.Country;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CountryService {

    public boolean isAllowed(String country) {
        if (country == null || country.isBlank()) {
            return false;
        }

        return Arrays.stream(Country.values())
                .anyMatch(allowedCountry -> allowedCountry.name().equalsIgnoreCase(country));
    }
}
