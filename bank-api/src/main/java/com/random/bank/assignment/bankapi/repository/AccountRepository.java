package com.random.bank.assignment.bankapi.repository;

import com.random.bank.assignment.bankapi.entity.Account;
import com.random.bank.assignment.bankapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByIban(String iban);

    Optional<Account> findByCustomer(Customer customer);
}
