package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import com.brokerage.stockorder.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerRepository customerRepository;

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
}