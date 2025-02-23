package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.model.Order;
import com.brokerage.stockorder.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Operations", description = "Admin-only APIs for managing orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Match a pending order",
            description = "Matches a pending order and updates the relevant assets. Only admin users can perform this operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order matched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order status or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/orders/{orderId}/match")
    public ResponseEntity<Order> matchOrder(
            @Parameter(description = "Order ID to match", required = true)
            @PathVariable String orderId) {
        return ResponseEntity.ok(adminService.matchOrder(orderId));
    }
}