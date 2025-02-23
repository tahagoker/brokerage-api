package com.brokerage.stockorder.repository;

import com.brokerage.stockorder.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
  Optional<Customer> findByUserName(String username);
  //Optional<Customer> findById(String customerId);
  boolean existsByUserName(String username);
}
