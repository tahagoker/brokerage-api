package com.brokerage.stockorder.service;

import com.brokerage.stockorder.constants.Assets;
import com.brokerage.stockorder.exception.OrderNotFoundException;
import com.brokerage.stockorder.exception.InvalidOrderStatusException;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.model.Order;
import com.brokerage.stockorder.constants.Status;
import com.brokerage.stockorder.constants.Side;
import com.brokerage.stockorder.repository.AssetRepository;
import com.brokerage.stockorder.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AssetRepository assetRepository;


    @Transactional
    public Order matchOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != Status.PENDING) {
            throw new InvalidOrderStatusException("Only PENDING orders can be matched. Current status: " + order.getStatus());
        }

        // Get customer's assets
        Asset customerTryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(),
                Assets.TRY.name())
                .orElseThrow(() -> new RuntimeException("Customer TRY asset not found"));

        Asset customerAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(), order.getAssetName())
                .orElse(createNewAsset(order.getCustomer(), order.getAssetName()));

        // Process the order based on side (BUY/SELL)
        if (order.getOrderSide() == Side.BUY) {
            processBuyOrder(order, customerTryAsset, customerAsset);
        } else {
            processSellOrder(order, customerTryAsset, customerAsset);
        }

        // Update order status to MATCHED
        order.setStatus(Status.MATCHED);
        return orderRepository.save(order);
    }

    private void processBuyOrder(Order order, Asset tryAsset, Asset targetAsset) {
        BigDecimal totalCost = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));
        
        // Update TRY asset (decrease usable size and size by total cost)
        tryAsset.setSize(tryAsset.getSize().subtract(totalCost));
        tryAsset.setUsableSize(tryAsset.getUsableSize().add(totalCost));
        assetRepository.save(tryAsset);

        // Update target asset (increase size by order size)
        targetAsset.setSize(targetAsset.getSize().add(BigDecimal.valueOf(order.getSize())));
        assetRepository.save(targetAsset);
    }

    private void processSellOrder(Order order, Asset tryAsset, Asset targetAsset) {
        BigDecimal totalValue = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));

        // Update target asset (decrease size and usable size by order size)
        targetAsset.setSize(targetAsset.getSize().subtract(BigDecimal.valueOf(order.getSize())));
        targetAsset.setUsableSize(targetAsset.getUsableSize().add(BigDecimal.valueOf(order.getSize())));
        assetRepository.save(targetAsset);

        // Update TRY asset (increase size by total value)
        tryAsset.setSize(tryAsset.getSize().add(totalValue));
        assetRepository.save(tryAsset);
    }

    private Asset createNewAsset(Customer customer, String assetName) {
        Asset newAsset = new Asset();
        newAsset.setCustomer(customer);
        newAsset.setAssetName(assetName);
        newAsset.setSize(BigDecimal.ZERO);
        newAsset.setUsableSize(BigDecimal.ZERO);
        return assetRepository.save(newAsset);
    }
}