package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.AccountOverviewResponse;
import com.random.bank.assignment.bankapi.entity.Account;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.exception.NotFoundException;
import com.random.bank.assignment.bankapi.repository.AccountRepository;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountOverviewService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AccountOverviewService(CustomerRepository customerRepository,
                                  AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    public AccountOverviewResponse getOverview(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }

        Customer customer = customerRepository.findByUsername(username.trim())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Account account = accountRepository.findByCustomer(customer)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        return new AccountOverviewResponse(
                account.getIban(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency()
        );
    }
}
