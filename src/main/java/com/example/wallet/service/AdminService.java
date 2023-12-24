package com.example.wallet.service;

import com.example.wallet.model.Admin;
import com.example.wallet.model.WalletTransaction;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {
    public void approveVendor(int adminId, int vendorId);

    public void createAdmin(Admin admin);

    public void approveUser(int adminId, int userId);

    public void reviewTransaction(int adminId, int transactionId, Boolean approve);

    public List<WalletTransaction> getFlaggedTransactions();
}
