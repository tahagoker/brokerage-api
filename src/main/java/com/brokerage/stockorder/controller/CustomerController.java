package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.dto.LoginRequestDto;
import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.dto.RegisterRequestDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.service.AuthenticationService;
import com.brokerage.stockorder.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Customer Authentication", description = "APIs for customer registration and authentication")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    AuthenticationService authenticationService;

    @Operation(summary = "Register a new customer",
               description = "Creates a new customer account with the provided credentials")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer registered successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or username already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<Customer> register(
            @Parameter(description = "Registration details including username and password", required = true)
            @Valid @RequestBody RegisterRequestDto registerRequest) {
        log.info("Register request received for username: {}", registerRequest.getUsername());
        return ResponseEntity.ok(customerService.registerCustomer(registerRequest.getUsername(),
            registerRequest.getPassword()));
    }

    @Operation(summary = "Authenticate customer",
               description = "Authenticates customer credentials and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        LoginResponseDto loginResponse = authenticationService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }
}