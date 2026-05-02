package com.random.bank.assignment.bankapi.controller;

import com.random.bank.assignment.bankapi.dto.AccountOverviewResponse;
import com.random.bank.assignment.bankapi.service.AccountOverviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountOverviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountOverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountOverviewService accountOverviewService;

    @Test
    void shouldReturnAccountOverview() throws Exception {
        AccountOverviewResponse response = new AccountOverviewResponse(
                "NL93RBAN7353543491",
                "CURRENT",
                BigDecimal.ZERO,
                "EUR"
        );

        when(accountOverviewService.getOverview("Bearer test-token")).thenReturn(response);

        mockMvc.perform(get("/overview")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("NL93RBAN7353543491"))
                .andExpect(jsonPath("$.accountType").value("CURRENT"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnBadRequestWhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/overview"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}