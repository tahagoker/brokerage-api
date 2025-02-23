package com.brokerage.stockorder.controller;

import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/money")
public class MoneyController {

  @Autowired
  AssetService assetService;

  @PostMapping("/deposit")
  public ResponseEntity<Asset> deposit(@RequestParam String customerId,
      @RequestParam BigDecimal size) {
    return ResponseEntity.ok(assetService.depositMoneyAsset(customerId, size));
  }

  @PostMapping("/withdraw")
  public ResponseEntity<Asset> withdraw(@RequestParam String customerId,
      @RequestParam BigDecimal size,
      @RequestParam String iban) {
    return ResponseEntity.ok(assetService.withdrawMoneyAsset(customerId, size, iban));
  }
}
