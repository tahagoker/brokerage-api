package com.brokerage.stockorder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.brokerage.stockorder.constants.Side;
import com.brokerage.stockorder.constants.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "orderId")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    @JsonIgnore
    private Customer customer;

    private String assetName;

    private Side orderSide;

    private BigDecimal size;

    private BigDecimal price;

    private Status status;

    @CreationTimestamp
    private LocalDateTime createDate;
}
