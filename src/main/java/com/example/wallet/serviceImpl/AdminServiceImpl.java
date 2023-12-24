package com.example.wallet.serviceImpl;

import com.example.wallet.exception.CustomException;
import com.example.wallet.model.*;
import com.example.wallet.repository.*;
import com.example.wallet.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    VendorRepository vendorRepository;
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletTransactionRepository transactionRepository;
    @Autowired
    WalletRepository walletRepository;

    @Override
    public void createAdmin(Admin admin) {
        admin.setCompanyWallet(new Wallet());
        adminRepository.save(admin);
    }

    @Override
    public void approveUser(int adminId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
        adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Admin not found"));

        user.setRegistrationStatus(User.RegistrationStatus.APPROVED);

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setOfflineBalance(BigDecimal.ZERO);
            user.setWallet(wallet);
        }
        userRepository.save(user);
    }

    @Override
    public void reviewTransaction(int adminId, int transactionId, Boolean approve) {

        adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Admin not found"));

        WalletTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Transaction not found"));

        if (approve) {
            Vendor vendor = vendorRepository.findById(transaction.getVendor().getVendorId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Vendor not found"));

            Wallet vendorWallet = vendor.getStoreWallet();
            vendorWallet.setBalance(vendorWallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(vendorWallet);

            transaction.setStatus(WalletTransaction.TransactionStatus.APPROVED);
            transactionRepository.save(transaction);
        } else {
            User user = userRepository.findById(transaction.getUser().getUserId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

            Wallet userWallet = user.getWallet();
            userWallet.setBalance(userWallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(userWallet);

            transaction.setStatus(WalletTransaction.TransactionStatus.REJECTED);
            transactionRepository.save(transaction);
        }
    }

    @Override
    public List<WalletTransaction> getFlaggedTransactions() {
        return transactionRepository.findByStatus(WalletTransaction.TransactionStatus.FLAGGED).get();
    }

    @Override
    public void approveVendor(int adminId, int vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Vendor not found"));
        adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Admin not found"));

        vendor.setRegistrationStatus(Vendor.RegistrationStatus.APPROVED);
        Wallet wallet = vendor.getStoreWallet();
        if (wallet == null) {
            wallet = new Wallet();
            vendor.setStoreWallet(wallet);
        }
        vendorRepository.save(vendor);
    }
}
