package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.AccountOverviewResponse;
import com.random.bank.assignment.bankapi.entity.Account;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.exception.NotFoundException;
import com.random.bank.assignment.bankapi.repository.AccountRepository;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountOverviewService {

    private static final Logger log = LoggerFactory.getLogger(AccountOverviewService.class);

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AccountOverviewService(CustomerRepository customerRepository,
                                  AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    public AccountOverviewResponse getOverview(String authorizationHeader) {

        String token = extractToken(authorizationHeader);

        log.info("Account overview requested using token");

        Customer customer = customerRepository.findBySessionToken(token)
                .orElseThrow(() -> {
                    log.warn("Account overview failed. Invalid or expired token");
                    return new BadRequestException("Invalid or expired login token");
                });

        Account account = accountRepository.findByCustomer(customer)
                .orElseThrow(() -> {
                    log.warn("Account not found for customer: {}", customer.getUsername());
                    return new NotFoundException("Account not found");
                });

        log.info("Account overview returned successfully for username: {}", customer.getUsername());

        return new AccountOverviewResponse(
                account.getIban(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            log.warn("Missing Authorization header");
            throw new BadRequestException("Authorization header is required");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header format");
            throw new BadRequestException("Authorization header must start with Bearer");
        }

        return authorizationHeader.substring(7).trim();
    }
}