package com.brokerage.stockorder.service;

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

    public Customer registerCustomer(String username, String password) {
        return customerRepository.save(Customer.builder()
            .userName(username)
            .password(passwordEncoder.encode(password))
            .build());
    }

    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Önce application.yml'daki admin kullanıcısını kontrol et
        if ("admin".equals(username)) {
            return User.builder()
                .username("admin")
                .password("$2a$10$CwPB0BLIR875vHHhBzqsbOIvEORbz/CR1jvP5VlFh6MD9oTU1LxYG")
                .roles("ADMIN")
                .build();
        }

        // Veritabanındaki kullanıcıları kontrol et
        Customer customer = customerRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
            .username(customer.getUserName())
            .password(customer.getPassword())
            .roles("USER")
            .build();
    }
}