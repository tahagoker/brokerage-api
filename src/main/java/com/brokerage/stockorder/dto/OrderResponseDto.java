package com.brokerage.stockorder.dto;

import com.brokerage.stockorder.constants.Side;
import com.brokerage.stockorder.constants.Status;
import com.brokerage.stockorder.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private String id;
    private String assetName;
    private Side side;
    private BigDecimal size;
    private BigDecimal price;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponseDto fromOrder(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setAssetName(order.getAssetName());
        dto.setSide(order.getOrderSide());
        dto.setSize(order.getSize());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreateDate());
        return dto;
    }
}