package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import com.brokerage.stockorder.security.JwtTokenProvider;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private final JwtTokenProvider jwtTokenProvider;



  public Customer registerCustomer(String username, String password) {
    return customerRepository.save(Customer.builder().userName(username)
        .password(passwordEncoder.encode(password)).build());
  }

  public Customer getCustomer(String customerId) {
    return customerRepository.findById(customerId).orElse(null);
  }

  @Transactional
  public LoginResponseDto login(String username, String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);


    String token = jwtTokenProvider.createToken(username,
        (Collection<? extends GrantedAuthority>) authentication.getAuthorities());

    Customer user = customerRepository.findByUserName(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return new LoginResponseDto(token, user.getId(), "Login successful");

  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new User(customer.getUserName(),
        customer.getPassword(),
        Collections.emptyList());
  }
}
