package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.model.Order;
import com.brokerage.stockorder.dto.CreateOrderRequestDto;
import com.brokerage.stockorder.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

  @Autowired
  OrderService orderService;

  @PostMapping()
  public ResponseEntity<Order> createOrder(
      @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {
    return ResponseEntity.ok(orderService.createOrder(createOrderRequestDto));
  }

  @DeleteMapping()
  public ResponseEntity<ResponseEntity.BodyBuilder> deleteOrder(@RequestParam String orderId) {
    orderService.deleteOrder(orderId);
    return ResponseEntity.ok().build();
  }

  @GetMapping()
  public ResponseEntity<List<Order>> listOrders(@RequestParam String customerId) {
    return ResponseEntity.ok(orderService.getOrderListForCustomer(customerId));
  }
}
