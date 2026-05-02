package com.random.bank.assignment.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.random.bank.assignment.bankapi.dto.RegisterRequest;
import com.random.bank.assignment.bankapi.dto.RegisterResponse;
import com.random.bank.assignment.bankapi.exception.BadRequestException;
import com.random.bank.assignment.bankapi.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void shouldRegisterCustomer() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Kunal",
                "Amsterdam",
                "kunal123",
                LocalDate.of(1998, 5, 10),
                "NL"
        );

        RegisterResponse response = new RegisterResponse("kunal123", "Pass123456");

        when(registrationService.register(request)).thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("kunal123"))
                .andExpect(jsonPath("$.password").value("Pass123456"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
        String request = """
                {
                  "address": "Amsterdam",
                  "username": "kunal123",
                  "dateOfBirth": "1998-05-10",
                  "country": "NL"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());


    }

    @Test
    void shouldReturnBadRequestWhenRegistrationFails() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Kunal",
                "Amsterdam",
                "kunal123",
                LocalDate.of(1998, 5, 10),
                "NL"
        );

        when(registrationService.register(request))
                .thenThrow(new BadRequestException("Username already exists"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }
}
