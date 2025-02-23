package com.brokerage.stockorder.repository;

import com.brokerage.stockorder.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
}
