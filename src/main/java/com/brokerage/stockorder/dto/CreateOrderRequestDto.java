package com.brokerage.stockorder.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.brokerage.stockorder.constants.Side;
import com.brokerage.stockorder.constants.Status;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.model.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderRequestDto {

  private String customerId;

  private String assetName;

  private Side orderSide;

  private BigDecimal size;

  @NotNull
  @Positive
  private BigDecimal price;


  @JsonIgnore
  public BigDecimal getTotalPrice() {
    return this.getSize().multiply(this.getPrice());
  }

  @JsonIgnore
  public Order toOrder() {
    return Order.builder()
        .customer(new Customer(customerId))
        .assetName(assetName)
        .orderSide(this.orderSide)
        .size(this.size)
        .price(this.price)
        .status(Status.PENDING)
        .build();
  }

  @Override
  public String toString() {
    return String.format("customerId: %s assetName: %s orderSide: %s size: %s price: %s",
        customerId, assetName, orderSide.name(), size, price);
  }
}
