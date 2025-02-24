package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.RegisterResponseDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponseDto registerCustomer(String username, String password) {
        Customer customer = customerRepository.save(Customer.builder()
            .userName(username)
            .password(passwordEncoder.encode(password))
            .build());
            
        return RegisterResponseDto.builder()
            .id(customer.getId())
            .username(customer.getUserName())
            .build();
    }

    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                .username("admin")
                .password("$2a$10$CwPB0BLIR875vHHhBzqsbOIvEORbz/CR1jvP5VlFh6MD9oTU1LxYG")
                .roles("ADMIN")
                .build();
        }

        Customer customer = customerRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
            .username(customer.getUserName())
            .password(customer.getPassword())
            .roles("USER")
            .build();
    }
}