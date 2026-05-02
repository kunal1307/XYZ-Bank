package com.random.bank.assignment.bankapi.repository;

import com.random.bank.assignment.bankapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findBySessionToken(String sessionToken);
}
