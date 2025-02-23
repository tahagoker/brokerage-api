package com.brokerage.stockorder.service;

import com.brokerage.stockorder.constants.Assets;
import com.brokerage.stockorder.exception.BaseException;
import com.brokerage.stockorder.model.Asset;
import com.brokerage.stockorder.model.Customer;
import com.brokerage.stockorder.repository.AssetRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private AssetService assetService;

    private final String customerId = "567ae529-5d31-4482-be17-fc27b85b9619";
    private final String goldAssetName = "BTC";


    private BigDecimal size = BigDecimal.valueOf(100);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateAsset(){

        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArgument(0));
        Asset createdAsset = assetService.createAsset(customerId, goldAssetName, BigDecimal.valueOf(100));

        BigDecimal assetSize = createdAsset.getSize();
        BigDecimal assetUsableSize = createdAsset.getUsableSize();

        assertNotNull(createdAsset);
        assertTrue(size.equals(assetSize) && assetSize.equals(assetUsableSize));
        assertEquals(customerId, createdAsset.getCustomer().getId());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    public void testDepositMoneyAsset_noExistingAsset(){
        Asset asset = Asset.builder().customer(new Customer(customerId)).assetName(goldAssetName).size(size)
                .usableSize(size).build();

        Mockito.when(assetService.getAsset(customerId, Assets.TRY.name())).thenReturn(null);
        Mockito.when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        Asset result = assetService.depositMoneyAsset(customerId, size);

        assertEquals(asset.getSize(), result.getSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
        assertNotNull(result);
    }

    @Test
    public void testDepositMoneyAsset_existingAsset(){
        Asset asset = Asset.builder().assetName(Assets.TRY.name()).size(size).usableSize(size).build();
        Customer customer = Customer.builder().assets(List.of(asset)).build();

        Mockito.when(customerService.getCustomer(customerId)).thenReturn(customer);
        Mockito.when(assetRepository.save(any(Asset.class))).thenAnswer(i -> i.getArguments()[0]);

        Asset result = assetService.depositMoneyAsset(customerId, BigDecimal.TEN);

        assertEquals(size.add(BigDecimal.TEN), result.getSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
        assertNotNull(result);
    }

    @Test
    public void testWithdrawMoneyAsset_sufficientFunds() {
        Asset moneyAsset = Asset.builder().assetName(Assets.TRY.name()).size(size).usableSize(size).build();
        Customer customer = Customer.builder().assets(List.of(moneyAsset)).build();

        Mockito.when(customerService.getCustomer(customerId)).thenReturn(customer);
        Mockito.when(assetRepository.save(any(Asset.class))).thenReturn(moneyAsset);

        Asset result = assetService.withdrawMoneyAsset(customerId, size);

        assertEquals(moneyAsset.getSize(), result.getSize());
        verify(assetRepository, times(1)).save(any(Asset.class));
        assertNotNull(result);
    }

    @Test
    public void testWithdrawMoneyAsset_insufficientFunds() {
        BigDecimal withdrawAmount = size.add(BigDecimal.ONE);
        Asset moneyAsset = Asset.builder().assetName(Assets.TRY.name()).size(size).usableSize(size).build();
        Customer customer = Customer.builder().assets(List.of(moneyAsset)).build();

        Mockito.when(customerService.getCustomer(customerId)).thenReturn(customer);

        Exception exception = assertThrows(BaseException.class, () ->
                assetService.withdrawMoneyAsset(customerId, withdrawAmount));

        assertEquals("Insufficient funds", exception.getMessage());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, ((BaseException) exception).getStatus());
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    public void testWithdrawMoneyAsset_invalidIBAN() {
        String invalidIBAN = "invalid iban";
        String exceptionMessage = "Invalid IBAN: " + invalidIBAN;
        Exception exception = assertThrows(BaseException.class, () ->
                assetService.withdrawMoneyAsset(customerId, size));

        assertEquals(exceptionMessage, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ((BaseException) exception).getStatus());
        verify(assetRepository, never()).save(any(Asset.class));
    }
}
