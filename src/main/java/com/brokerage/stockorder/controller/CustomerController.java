package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.dto.LoginRequestDto;
import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.dto.RegisterRequestDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.service.AuthenticationService;
import com.brokerage.stockorder.service.CustomerService;
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
public class CustomerController {

  @Autowired
  CustomerService customerService;


  @Autowired
  AuthenticationService authenticationService;

  /**
   * Register endpoint to create new user account
   *
   * @param registerRequest Registration details
   * @return Registration confirmation
   */
  @PostMapping("/register")
  public ResponseEntity<Customer> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
    log.info("Register request received for username: {}", registerRequest.getUsername());
    return ResponseEntity.ok(customerService.registerCustomer(registerRequest.getUsername(),
        registerRequest.getPassword()));
  }

  /**
   * Login endpoint to authenticate user and generate JWT token
   *
   * @param loginRequest Login credentials
   * @return JWT token and success message
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
    log.info("Login attempt for user: {}", loginRequest.getUsername());
    LoginResponseDto loginResponse = authenticationService.login(loginRequest.getUsername(), loginRequest.getPassword());
    return ResponseEntity.ok(loginResponse);
  }
}
