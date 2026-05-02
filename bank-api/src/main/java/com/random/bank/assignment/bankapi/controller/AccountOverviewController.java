package com.random.bank.assignment.bankapi.controller;

import com.random.bank.assignment.bankapi.dto.AccountOverviewResponse;
import com.random.bank.assignment.bankapi.service.AccountOverviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/overview")
public class AccountOverviewController {

    private final AccountOverviewService accountOverviewService;

    public AccountOverviewController(AccountOverviewService accountOverviewService) {
        this.accountOverviewService = accountOverviewService;
    }

    @GetMapping
    public AccountOverviewResponse getOverview(@RequestHeader("Authorization") String authorizationHeader) {
        return accountOverviewService.getOverview(authorizationHeader);
    }
}
