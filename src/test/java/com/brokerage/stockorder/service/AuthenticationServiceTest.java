package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.LoginResponseDto;
import com.brokerage.stockorder.exception.AuthenticationException;
import com.brokerage.stockorder.exception.InvalidCredentialsException;
import com.brokerage.stockorder.exception.UserNotFoundException;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import com.brokerage.stockorder.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String TOKEN = "testToken";
    private static final String USER_ID = "46a1e18a-9ba6-4f2d-8021-f2d20f7c877d";

    private Customer customer;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(USER_ID);
        customer.setUserName(USERNAME);

        authentication = new UsernamePasswordAuthenticationToken(
            USERNAME,
            PASSWORD,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void login_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.createToken(eq(USERNAME), any()))
            .thenReturn(TOKEN);
        when(customerRepository.findByUserName(USERNAME))
            .thenReturn(Optional.of(customer));

        // Act
        LoginResponseDto response = authenticationService.login(USERNAME, PASSWORD);

        // Assert
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        assertEquals(USER_ID, response.getCustomerId());
        assertEquals("Login successful", response.getMessage());

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(eq(USERNAME), any());
        verify(customerRepository).findByUserName(USERNAME);
    }

    @Test
    void login_InvalidCredentials_ThrowsInvalidCredentialsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
            () -> authenticationService.login(USERNAME, PASSWORD));
        assertEquals("Invalid username or password", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(any(), any());
        verify(customerRepository, never()).findByUserName(any());
    }

    @Test
    void login_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.createToken(eq(USERNAME), any()))
            .thenReturn(TOKEN);
        when(customerRepository.findByUserName(USERNAME))
            .thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
            () -> authenticationService.login(USERNAME, PASSWORD));
        assertEquals("User not found with username: " + USERNAME, exception.getMessage());

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(eq(USERNAME), any());
        verify(customerRepository).findByUserName(USERNAME);
    }

    @Test
    void login_DisabledAccount_ThrowsAuthenticationException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new DisabledException("Account is disabled"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authenticationService.login(USERNAME, PASSWORD));
        assertEquals("Account is disabled", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(any(), any());
        verify(customerRepository, never()).findByUserName(any());
    }

    @Test
    void login_LockedAccount_ThrowsAuthenticationException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new LockedException("Account is locked"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authenticationService.login(USERNAME, PASSWORD));
        assertEquals("Account is locked", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(any(), any());
        verify(customerRepository, never()).findByUserName(any());
    }
}