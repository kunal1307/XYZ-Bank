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

        when(customerRepository.findBySessionToken("test-token"))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomer(customer))
                .thenReturn(Optional.of(account));

        AccountOverviewResponse response = accountOverviewService.getOverview("Bearer test-token");

        assertEquals("NL93RBAN7353543491", response.accountNumber());
        assertEquals("CURRENT", response.accountType());
        assertEquals(BigDecimal.ZERO, response.balance());
        assertEquals("EUR", response.currency());
    }

    @Test
    void shouldFailWhenAuthorizationHeaderIsMissing() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> accountOverviewService.getOverview(" ")
        );

        assertEquals("Authorization header is required", exception.getMessage());
    }

    @Test
    void shouldFailWhenAuthorizationHeaderFormatIsInvalid() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> accountOverviewService.getOverview("test-token")
        );

        assertEquals("Authorization header must start with Bearer", exception.getMessage());
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        when(customerRepository.findBySessionToken("invalid-token"))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> accountOverviewService.getOverview("Bearer invalid-token")
        );

        assertEquals("Invalid or expired login token", exception.getMessage());
    }

    @Test
    void shouldFailWhenAccountNotFound() {
        Customer customer = Customer.builder()
                .username("kunal123")
                .build();

        when(customerRepository.findBySessionToken("test-token"))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomer(customer))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accountOverviewService.getOverview("Bearer test-token")
        );

        assertEquals("Account not found", exception.getMessage());
    }
}