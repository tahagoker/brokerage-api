package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.dto.CreateAssetRequestDto;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.service.AssetService;
import org.springdoc.core.annotations.ParameterObject;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asset")
@Tag(name = "Asset Management", description = "APIs for managing customer assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Operation(summary = "List customer assets", 
               description = "Retrieves all assets owned by a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assets retrieved successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Asset.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/list")
    public ResponseEntity<List<Asset>> listAssets(
            @Parameter(description = "Customer's unique identifier", required = true)
            @RequestParam String customerId) {
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }

    @Operation(summary = "Create a new asset",
               description = "Creates a new asset for a customer with specified details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asset created successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Asset.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Asset> createAsset(
            @Parameter(description = "Asset creation details", required = true)
            @Valid @RequestBody CreateAssetRequestDto createAssetRequestDto) {
        return ResponseEntity.ok(
            assetService.createAsset(createAssetRequestDto.getCustomerId(),
                createAssetRequestDto.getAssetName(), createAssetRequestDto.getSize()));
    }

    @Operation(summary = "Deposit money into customer's account",
               description = "Deposits a specified amount of money into customer's money asset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Asset.class))),
        @ApiResponse(responseCode = "400", description = "Invalid amount"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/deposit")
    public ResponseEntity<Asset> deposit(
            @Parameter(description = "Customer's unique identifier", required = true)
            @RequestParam String customerId,
            @Parameter(description = "Amount to deposit", required = true)
            @RequestParam BigDecimal size) {
        return ResponseEntity.ok(assetService.depositMoneyAsset(customerId, size));
    }

    @Operation(summary = "Withdraw money from customer's account",
               description = "Withdraws a specified amount of money from customer's money asset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Asset.class))),
        @ApiResponse(responseCode = "400", description = "Invalid amount or insufficient funds"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Asset> withdraw(
            @Parameter(description = "Customer's unique identifier", required = true)
            @RequestParam String customerId,
            @Parameter(description = "Amount to withdraw", required = true)
            @RequestParam BigDecimal size) {
        return ResponseEntity.ok(assetService.withdrawMoneyAsset(customerId, size));
    }
}