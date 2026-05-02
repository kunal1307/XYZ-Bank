package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.RegisterRequest;
import com.random.bank.assignment.bankapi.dto.RegisterResponse;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.repository.AccountRepository;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;
    private CountryService countryService;
    private AgeValidator ageValidator;
    private PasswordGenerator passwordGenerator;
    private IbanGenerator ibanGenerator;

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        accountRepository = mock(AccountRepository.class);
        countryService = mock(CountryService.class);
        ageValidator = mock(AgeValidator.class);
        passwordGenerator = mock(PasswordGenerator.class);
        ibanGenerator = mock(IbanGenerator.class);

        registrationService = new RegistrationService(
                customerRepository,
                accountRepository,
                countryService,
                ageValidator,
                passwordGenerator,
                ibanGenerator
        );
    }

    @Test
    void shouldRegisterCustomerAndCreateAccount() {
        RegisterRequest request = new RegisterRequest(
                "Kunal",
                "Amsterdam",
                "kunal123",
                LocalDate.of(1998, 5, 10),
                "NL"
        );

        when(customerRepository.existsByUsername("kunal123")).thenReturn(false);
        when(countryService.isAllowed("NL")).thenReturn(true);
        when(ageValidator.isAdult(request.dateOfBirth())).thenReturn(true);
        when(passwordGenerator.generate()).thenReturn("Pass123456");
        when(ibanGenerator.generate()).thenReturn("NL93RBAN7353543491");
        when(accountRepository.existsByIban("NL93RBAN7353543491")).thenReturn(false);

        Customer savedCustomer = Customer.builder()
                .id(1L)
                .name("Kunal")
                .address("Amsterdam")
                .username("kunal123")
                .dateOfBirth(request.dateOfBirth())
                .password("Pass123456")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        RegisterResponse response = registrationService.register(request);

        assertEquals("kunal123", response.username());
        assertEquals("Pass123456", response.password());

        verify(customerRepository).save(any(Customer.class));
        verify(accountRepository).save(any());
    }

    @Test
    void shouldFailWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Kunal",
                "Amsterdam",
                "kunal123",
                LocalDate.of(1998, 5, 10),
                "NL"
        );

        when(customerRepository.existsByUsername("kunal123")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> registrationService.register(request)
        );

        assertEquals("Username already exists", exception.getMessage());

        verify(customerRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenCountryIsNotAllowed() {
        RegisterRequest request = new RegisterRequest(
                "John",
                "London",
                "john123",
                LocalDate.of(1995, 1, 1),
                "UK"
        );

        when(customerRepository.existsByUsername("john123")).thenReturn(false);
        when(countryService.isAllowed("UK")).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> registrationService.register(request)
        );

        assertEquals("Registration is only allowed for customers from Netherlands and Belgium", exception.getMessage());

        verify(customerRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenCustomerIsUnder18() {
        RegisterRequest request = new RegisterRequest(
                "Young User",
                "Brussels",
                "young123",
                LocalDate.now().minusYears(16),
                "BE"
        );

        when(customerRepository.existsByUsername("young123")).thenReturn(false);
        when(countryService.isAllowed("BE")).thenReturn(true);
        when(ageValidator.isAdult(request.dateOfBirth())).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> registrationService.register(request)
        );

        assertEquals("Customer must be at least 18 years old", exception.getMessage());

        verify(customerRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }
}
