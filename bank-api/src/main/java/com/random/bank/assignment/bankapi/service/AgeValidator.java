package com.random.bank.assignment.bankapi.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AgeValidator {

    public boolean isAdult(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }

        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }
}
