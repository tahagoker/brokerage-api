package com.brokerage.stockorder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "assets")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assetId")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    @JsonIgnore
    private Customer customer;

    private String assetName;

    private BigDecimal size;

    private BigDecimal usableSize;

    public void deposit(BigDecimal depositAmount) {
        this.size = this.size.add(depositAmount);
        this.usableSize = this.usableSize.add(depositAmount);
    }

    public boolean withdraw(BigDecimal withdrawAmount) {
        if(this.usableSize.compareTo(withdrawAmount) >= 0) {
            this.size = this.size.subtract(withdrawAmount);
            this.usableSize = this.usableSize.subtract(withdrawAmount);
            return true;
        }
        return false;
    }
}
