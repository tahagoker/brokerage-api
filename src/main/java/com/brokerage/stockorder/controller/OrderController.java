package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.model.Order;
import com.brokerage.stockorder.dto.CreateOrderRequestDto;
import com.brokerage.stockorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@Tag(name = "Order Management", description = "APIs for managing trading orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Create a new order",
               description = "Creates a new trading order for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order data"),
        @ApiResponse(responseCode = "404", description = "Customer or asset not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "Order creation details including customer ID, asset, and quantity", required = true)
            @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(createOrderRequestDto));
    }

    @Operation(summary = "Delete an order",
               description = "Deletes an existing order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping()
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order's unique identifier", required = true)
            @RequestParam String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List customer orders",
               description = "Retrieves all orders for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    public ResponseEntity<List<Order>> listOrders(
            @Parameter(description = "Customer's unique identifier", required = true)
            @RequestParam String customerId) {
        return ResponseEntity.ok(orderService.getOrderListForCustomer(customerId));
    }
}