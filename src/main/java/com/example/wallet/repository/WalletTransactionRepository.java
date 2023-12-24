package com.example.wallet.repository;

import com.example.wallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {

    Optional<List<WalletTransaction>> findByStatus(WalletTransaction.TransactionStatus transactionStatus);
}
