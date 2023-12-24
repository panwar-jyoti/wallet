package com.example.wallet.service;


import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface UserService {
    public void enrollForOfflinePayment(int id);

    public void addFundsToWallet(int id, long amount);

    public void transferMoneyToOffline(int userId, long amount);

    public Set<String> getOfflineCodes(int userId);

    public void makeOnlinePayment(int userId, int vendorId, long amount, String otp);

    public void makeOfflinePayment(int userId, int vendorId, long amount, double latitude, double longitude, String code);

    public String generateOTP(int userId);
}
