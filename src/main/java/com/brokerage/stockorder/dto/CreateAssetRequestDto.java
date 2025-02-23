package com.brokerage.stockorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateAssetRequestDto {

  @NotBlank(message = "customerId is required")
  String customerId;

  @NotBlank(message = "assetName is required")
  String assetName;

  @NotNull(message = "size is required")
  BigDecimal size;
}
