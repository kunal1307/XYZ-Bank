package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.LoginRequest;
import com.random.bank.assignment.bankapi.dto.LoginResponse;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private CustomerRepository customerRepository;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        loginService = new LoginService(customerRepository);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("kunal123", "Pass123456");

        Customer customer = Customer.builder()
                .username("kunal123")
                .password("Pass123456")
                .build();

        when(customerRepository.findByUsername("kunal123"))
                .thenReturn(Optional.of(customer));

        LoginResponse response = loginService.login(request);

        assertEquals("Login successful", response.message());
        assertNotNull(response.token());
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldFailWhenUsernameDoesNotExist() {
        LoginRequest request = new LoginRequest("unknown", "Pass123456");

        when(customerRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> loginService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void shouldFailWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("kunal123", "wrongPass");

        Customer customer = Customer.builder()
                .username("kunal123")
                .password("Pass123456")
                .build();

        when(customerRepository.findByUsername("kunal123"))
                .thenReturn(Optional.of(customer));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> loginService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }
}
