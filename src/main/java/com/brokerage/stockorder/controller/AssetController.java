package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.dto.CreateAssetRequestDto;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asset")
public class AssetController {

  @Autowired
  private AssetService assetService;

  @GetMapping("/list")
  public ResponseEntity<List<Asset>> listAssets(@RequestParam String customerId) {
    return ResponseEntity.ok(assetService.listAssets(customerId));
  }

  @PostMapping
  public ResponseEntity<Asset> createAsset(@Valid @RequestBody CreateAssetRequestDto createAssetRequestDto) {
    return ResponseEntity.ok(
        assetService.createAsset(createAssetRequestDto.getCustomerId(),
            createAssetRequestDto.getAssetName(), createAssetRequestDto.getSize()));
  }

  @PostMapping("/deposit")
  public ResponseEntity<Asset> deposit(@RequestParam String customerId,
      @RequestParam BigDecimal size) {
    return ResponseEntity.ok(assetService.depositMoneyAsset(customerId, size));
  }

  @PostMapping("/withdraw")
  public ResponseEntity<Asset> withdraw(@RequestParam String customerId,
      @RequestParam BigDecimal size) {
    return ResponseEntity.ok(assetService.withdrawMoneyAsset(customerId, size));
  }
}
