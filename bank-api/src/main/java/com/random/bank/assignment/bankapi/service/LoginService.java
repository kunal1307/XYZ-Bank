package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.LoginRequest;
import com.random.bank.assignment.bankapi.dto.LoginResponse;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final CustomerRepository customerRepository;

    public LoginService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!customer.getPassword().equals(request.password())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new LoginResponse("Login successful");
    }
}
