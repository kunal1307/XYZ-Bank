package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.AccountOverviewResponse;
import com.random.bank.assignment.bankapi.entity.Account;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.exception.NotFoundException;
import com.random.bank.assignment.bankapi.repository.AccountRepository;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountOverviewServiceTest {

    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;
    private AccountOverviewService accountOverviewService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        accountRepository = mock(AccountRepository.class);
        accountOverviewService = new AccountOverviewService(customerRepository, accountRepository);
    }

    @Test
    void shouldReturnAccountOverview() {
        Customer customer = Customer.builder()
                .username("kunal123")
                .build();

        Account account = Account.builder()
                .iban("NL93RBAN7353543491")
                .accountType("CURRENT")
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .customer(customer)
                .build();

        when(customerRepository.findByUsername("kunal123"))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomer(customer))
                .thenReturn(Optional.of(account));

        AccountOverviewResponse response = accountOverviewService.getOverview("kunal123");

        assertEquals("NL93RBAN7353543491", response.accountNumber());
        assertEquals("CURRENT", response.accountType());
        assertEquals(BigDecimal.ZERO, response.balance());
        assertEquals("EUR", response.currency());
    }

    @Test
    void shouldFailWhenUsernameIsBlank() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> accountOverviewService.getOverview(" ")
        );

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    void shouldFailWhenCustomerNotFound() {
        when(customerRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accountOverviewService.getOverview("unknown")
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void shouldFailWhenAccountNotFound() {
        Customer customer = Customer.builder()
                .username("kunal123")
                .build();

        when(customerRepository.findByUsername("kunal123"))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomer(customer))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accountOverviewService.getOverview("kunal123")
        );

        assertEquals("Account not found", exception.getMessage());
    }
}