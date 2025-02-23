package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.dto.RegisterRequestDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class CustomerController {

  @Autowired
  CustomerService customerService;

  @PostMapping("/register")
  public ResponseEntity<Customer> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
    return ResponseEntity.ok(customerService.registerCustomer(registerRequest.getUsername(),
        registerRequest.getPassword()));
  }
}
