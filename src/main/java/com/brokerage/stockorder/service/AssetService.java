package com.brokerage.stockorder.service;

import com.brokerage.stockorder.exception.BaseException;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.AssetRepository;
import com.brokerage.stockorder.util.AssetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private CustomerService customerService;

    public Asset depositMoneyAsset(String customerId, BigDecimal size){
        Asset asset = getMoneyAsset(customerId);
        if (asset != null) {
            asset.deposit(size);
            return assetRepository.save(asset);
        }
        return this.createAsset(customerId, AssetUtil.MONEY_ASSET, size);
    }

    public Asset withdrawMoneyAsset(String customerId, BigDecimal size, String iban){
        AssetUtil.validateIBAN(iban);
        Asset asset = getMoneyAsset(customerId);
        if (asset != null && asset.withdraw(size)) {
            return assetRepository.save(asset);
        }
        throw new BaseException("Insufficient funds", HttpStatus.NOT_ACCEPTABLE);
    }
    public Asset createAsset(String customerId, String assetName, BigDecimal size) {
        Asset asset = Asset.builder().customer(new Customer(customerId)).assetName(assetName).size(size)
                .usableSize(size).build();
        return assetRepository.save(asset);
    }

    public void updateAsset(Asset asset) {
       assetRepository.save(asset);
    }

    public Asset getAsset(String customerId, String assetName) {
        Customer customer = customerService.getCustomer(customerId);
        if (customer != null) {
            return customerService.getCustomer(customerId).getAssets().stream()
                    .filter(customerAsset -> customerAsset.getAssetName().equals(assetName)).findAny()
                    .orElse(null);
        }
        return null;
    }

    public Asset getMoneyAsset(String customerId) {
        return getAsset(customerId, AssetUtil.MONEY_ASSET);
    }

    public List<Asset> listAssets(String customerId) {
        Example<Asset> assetExample = Example.of(Asset.builder().customer(new Customer(customerId)).build());
        return assetRepository.findAll(assetExample);
    }
}
