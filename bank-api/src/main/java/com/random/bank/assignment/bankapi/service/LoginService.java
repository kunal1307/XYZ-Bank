package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.LoginRequest;
import com.random.bank.assignment.bankapi.dto.LoginResponse;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final CustomerRepository customerRepository;

    public LoginService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public LoginResponse login(LoginRequest request) {

        String username = request.username().trim();

        Customer customer = customerRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> {
                    log.warn("Login failed. Username not found: {}", username);
                    return new BadRequestException("Invalid username or password");
                });

        if (!customer.getPassword().equals(request.password())) {
            log.warn("Login failed. Invalid password for username: {}", username);
            throw new BadRequestException("Invalid username or password");
        }

        String token = UUID.randomUUID().toString();
        customer.setSessionToken(token);
        customerRepository.save(customer);

        log.info("Login successful for username: {}", username);

        return new LoginResponse("Login successful", token);
    }
}
