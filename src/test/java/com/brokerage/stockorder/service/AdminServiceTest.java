package com.brokerage.stockorder.service;

import com.brokerage.stockorder.constants.Status;
import com.brokerage.stockorder.constants.Side;
import com.brokerage.stockorder.exception.InvalidOrderStatusException;
import com.brokerage.stockorder.exception.OrderNotFoundException;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.model.Order;
import com.brokerage.stockorder.repository.AssetRepository;
import com.brokerage.stockorder.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AdminService adminService;

    private Order pendingOrder;
    private Asset tryAsset;
    private Asset stockAsset;
    private Customer customer;

    private final String orderId = "949cba2b-d97f-4237-a5e2-fd339e3b5248";
    private final String customerId = "fdced82d-fde0-4003-b054-caf2cd607a9a";

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(customerId);

        pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setCustomer(customer);
        pendingOrder.setAssetName("AAPL");
        pendingOrder.setStatus(Status.PENDING);
        pendingOrder.setSize(BigDecimal.valueOf(10));
        pendingOrder.setPrice(new BigDecimal("150.00"));

        tryAsset = new Asset();
        tryAsset.setCustomer(customer);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(new BigDecimal("2000.00"));
        tryAsset.setUsableSize(new BigDecimal("2000.00"));

        stockAsset = new Asset();
        stockAsset.setCustomer(customer);
        stockAsset.setAssetName("AAPL");
        stockAsset.setSize(new BigDecimal("100"));
        stockAsset.setUsableSize(new BigDecimal("100"));
    }

    @Test
    void matchOrder_BuyOrder_Success() {
        pendingOrder.setOrderSide(Side.BUY);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL")).thenReturn(Optional.of(stockAsset));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArguments()[0]);

        Order matchedOrder = adminService.matchOrder(orderId);

        assertEquals(Status.MATCHED, matchedOrder.getStatus());
        verify(orderRepository).save(pendingOrder);
        verify(assetRepository, times(3)).save(any(Asset.class));
    }

    @Test
    void matchOrder_SellOrder_Success() {
        pendingOrder.setOrderSide(Side.SELL);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL")).thenReturn(Optional.of(stockAsset));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArguments()[0]);

        Order matchedOrder = adminService.matchOrder(orderId);

        assertEquals(Status.MATCHED, matchedOrder.getStatus());
        verify(orderRepository).save(pendingOrder);
        verify(assetRepository, times(3)).save(any(Asset.class));
    }

    @Test
    void matchOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> adminService.matchOrder(orderId));
    }

    @Test
    void matchOrder_InvalidStatus() {
        pendingOrder.setStatus(Status.MATCHED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThrows(InvalidOrderStatusException.class, () -> adminService.matchOrder(orderId));
    }
}