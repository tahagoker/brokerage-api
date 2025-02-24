package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.exception.AuthenticationException;
import com.brokerage.stockorder.exception.InvalidCredentialsException;
import com.brokerage.stockorder.exception.UserNotFoundException;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import com.brokerage.stockorder.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.createToken(username,
                (Collection<? extends GrantedAuthority>) authentication.getAuthorities());

            Customer user = customerRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

            return new LoginResponseDto(token, user.getId(), "Login successful");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (DisabledException e) {
            throw new AuthenticationException("Account is disabled");
        } catch (LockedException e) {
            throw new AuthenticationException("Account is locked");
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}