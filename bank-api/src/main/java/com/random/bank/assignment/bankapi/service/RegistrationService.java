package com.random.bank.assignment.bankapi.service;

import com.random.bank.assignment.bankapi.dto.RegisterRequest;
import com.random.bank.assignment.bankapi.dto.RegisterResponse;
import com.random.bank.assignment.bankapi.entity.Account;
import com.random.bank.assignment.bankapi.entity.Country;
import com.random.bank.assignment.bankapi.entity.Customer;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.repository.AccountRepository;
import com.random.bank.assignment.bankapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CountryService countryService;
    private final AgeValidator ageValidator;
    private final PasswordGenerator passwordGenerator;
    private final IbanGenerator ibanGenerator;

    public RegistrationService(CustomerRepository customerRepository,
                               AccountRepository accountRepository,
                               CountryService countryService,
                               AgeValidator ageValidator,
                               PasswordGenerator passwordGenerator,
                               IbanGenerator ibanGenerator) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.countryService = countryService;
        this.ageValidator = ageValidator;
        this.passwordGenerator = passwordGenerator;
        this.ibanGenerator = ibanGenerator;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username().trim();

        log.info("Registration request received for username: {}", username);

        if (customerRepository.existsByUsername(username)) {
            log.warn("Registration failed. Username already exists: {}", username);
            throw new BadRequestException("Username already exists");
        }

        if (!countryService.isAllowed(request.country())) {
            log.warn("Registration failed. Unsupported country: {}", request.country());
            throw new BadRequestException("Registration is only allowed for customers from Netherlands and Belgium");
        }

        if (!ageValidator.isAdult(request.dateOfBirth())) {
            log.warn("Registration failed. Customer is under 18. Username: {}", username);
            throw new BadRequestException("Customer must be at least 18 years old");
        }

        String password = passwordGenerator.generate();

        Customer customer = Customer.builder()
                .name(request.name().trim())
                .address(request.address().trim())
                .username(username)
                .dateOfBirth(request.dateOfBirth())
                .country(Country.valueOf(request.country().trim().toUpperCase()))
                .password(password)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        String iban = generateUniqueIban();

        Account account = Account.builder()
                .iban(iban)
                .customer(savedCustomer)
                .accountType("CURRENT")
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .build();

        accountRepository.save(account);

        log.info("Customer registered successfully. Username: {}", savedCustomer.getUsername());

        return new RegisterResponse(savedCustomer.getUsername(), password);
    }

    private String generateUniqueIban() {
        String iban;

// The database unique constraint is still needed as the final protection against duplicates.
        do {
            iban = ibanGenerator.generate();
        } while (accountRepository.existsByIban(iban));

        return iban;
    }
}
