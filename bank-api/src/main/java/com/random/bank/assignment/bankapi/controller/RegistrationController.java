package com.random.bank.assignment.bankapi.controller;

import com.random.bank.assignment.bankapi.dto.RegisterRequest;
import com.random.bank.assignment.bankapi.dto.RegisterResponse;
import com.random.bank.assignment.bankapi.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }
}
