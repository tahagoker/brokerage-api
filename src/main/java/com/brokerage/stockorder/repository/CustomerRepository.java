package com.brokerage.stockorder.repository;

import com.brokerage.stockorder.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
