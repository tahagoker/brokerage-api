package com.brokerage.stockorder.service;

import com.brokerage.stockorder.dto.RegisterResponseDto;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.CustomerRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterCustomer(){
        String username = "testuser";
        String password = "123456";
        String id = "b42846d3-5136-4e33-a6c7-2948e2653b98";

        Customer mockCustomer = Customer.builder()
            .id(id)
            .userName(username)
            .password(password)
            .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(mockCustomer);
        RegisterResponseDto result = customerService.registerCustomer(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(id, result.getId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}