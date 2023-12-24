package com.example.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private BigDecimal offlineBalance;

    private BigDecimal balance;

    @ElementCollection
    @CollectionTable(name = "payment_codes", joinColumns = @JoinColumn(name = "walletId"))
    @Column(name = "code")
    @Builder.Default
    Set<String> payment_codes = new HashSet<>();
}
