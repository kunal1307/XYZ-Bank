package com.random.bank.assignment.bankapi.controller;

import com.random.bank.assignment.bankapi.dto.LoginRequest;
import com.random.bank.assignment.bankapi.dto.LoginResponse;
import com.random.bank.assignment.bankapi.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
