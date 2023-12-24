package com.example.wallet.serviceImpl;

import com.example.wallet.exception.CustomException;
import com.example.wallet.model.User;
import com.example.wallet.model.Vendor;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.repository.*;
import com.example.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final int EARTH_RADIUS = 6371;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    WalletTransactionRepository transactionRepository;
    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    AdminRepository adminRepository;

    @Override
    public void enrollForOfflinePayment(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
        if(!user.getIsOfflineEnrolled())
            user.setIsOfflineEnrolled(true);
        else
            throw new CustomException(HttpStatus.ALREADY_REPORTED, "User already enrolled");
    }

    @Override
    public void addFundsToWallet(int id, long amount) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        checkIfUserCanAccessFunctionalities(user);

        if(user.getWallet() != null) {
            System.out.println("Wallet found");
            Wallet wallet = user.getWallet();
            wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
            walletRepository.save(wallet);
        }
    }

    private void checkIfUserCanAccessFunctionalities(User user) {
        if(user.getRegistrationStatus()== User.RegistrationStatus.APPROVED)
            throw new CustomException(HttpStatus.BAD_REQUEST, "User not approved");
        Date date = new Date();
        if(System.currentTimeMillis() - user.getUpdatedAt().getTime() < 1000 * 60 * 15)
            throw new CustomException(HttpStatus.BAD_REQUEST, "Cooldown time");
    }

    @Override
    public void transferMoneyToOffline(int userId, long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        checkIfUserCanAccessFunctionalities(user);

        Wallet wallet = user.getWallet();
        if(wallet.getPayment_codes().isEmpty())
            wallet.setPayment_codes(generateOfflinePaymentCodes(wallet));

        wallet.setOfflineBalance(wallet.getOfflineBalance().add(BigDecimal.valueOf(amount)));
        wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        walletRepository.save(wallet);
    }

    @Override
    public Set<String> getOfflineCodes(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        checkIfUserCanAccessFunctionalities(user);

        Wallet wallet = user.getWallet();
        if(wallet.getPayment_codes().isEmpty())
            return new HashSet<>();

        return wallet.getPayment_codes();
    }

    @Override
    public void makeOnlinePayment(int userId, int vendorId, long amount, String otp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));;

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor with the above id not found error"));

        checkIfUserCanAccessFunctionalities(user);

        if(user.getOtp().compareTo(otp) == 0) {

            WalletTransaction transaction = new WalletTransaction();
            transaction.setUser(userRepository.getReferenceById(userId));
            transaction.setVendor(vendorRepository.getReferenceById(vendorId));
            transaction.setCreatedAt((java.sql.Date) new Date(System.currentTimeMillis()));
            transaction.setTransactionType(WalletTransaction.TransactionType.ONLINE);
            transaction.setStatus(WalletTransaction.TransactionStatus.APPROVED);
            transaction.setAmount(BigDecimal.valueOf(amount));

            Wallet userWallet = user.getWallet();
            userWallet.setBalance(userWallet.getBalance().subtract(BigDecimal.valueOf(amount)));
            walletRepository.save(userWallet);

            Wallet vendorWallet = vendor.getStoreWallet();
            vendorWallet.setBalance(vendorWallet.getBalance().add(BigDecimal.valueOf(amount)));
            walletRepository.save(vendorWallet);


            user.setOtp(null);

            try {
                transactionRepository.save(transaction);
            } catch (ObjectOptimisticLockingFailureException e) {
                System.out.println("Concurrent modification detected");
                e.printStackTrace();
            }
        }
        throw new CustomException(HttpStatus.BAD_REQUEST, "Incorrect OTP");
    }

    @Override
    public void makeOfflinePayment(int userId, int vendorId, long amount, double latitude, double longitude, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        checkIfUserCanAccessFunctionalities(user);

        Wallet userWallet = user.getWallet();

        if(userWallet.getPayment_codes().contains(code)) {
            WalletTransaction transaction = new WalletTransaction();
            transaction.setUser(userRepository.getReferenceById(userId));
            transaction.setVendor(vendorRepository.getReferenceById(vendorId));
            transaction.setCreatedAt((java.sql.Date) new Date(System.currentTimeMillis()));
            transaction.setTransactionType(WalletTransaction.TransactionType.OFFLINE);
            transaction.setAmount(BigDecimal.valueOf(amount));

            userWallet.setBalance(userWallet.getBalance().subtract(BigDecimal.valueOf(amount)));
            walletRepository.save(userWallet);

            if(isWithin20KMRadius(latitude, longitude, vendor.getLatitude(), vendor.getLongitude())) {
                transaction.setStatus(WalletTransaction.TransactionStatus.APPROVED);

                Wallet vendorWallet = vendor.getStoreWallet();
                vendorWallet.setBalance(vendorWallet.getBalance().add(BigDecimal.valueOf(amount)));
                walletRepository.save(vendorWallet);

                try {
                    transactionRepository.save(transaction);
                } catch (ObjectOptimisticLockingFailureException e) {
                    System.out.println("Concurrent modification detected");
                    e.printStackTrace();
                }
            } else {
                transaction.setStatus(WalletTransaction.TransactionStatus.FLAGGED);

                try {
                    transactionRepository.save(transaction);
                } catch (ObjectOptimisticLockingFailureException e) {
                    System.out.println("Concurrent modification detected");
                    e.printStackTrace();
                }
            }
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid code");
        }

    }

    @Override
    public String generateOTP(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for(int i=0; i<6; i++) {
            int index = random.nextInt(10);
            otp.append(index);
        }
        user.setOtp(otp.toString());
        userRepository.save(user);
        return otp.toString();
    }

    private boolean isWithin20KMRadius(double latitude, double longitude, double latitude1, double longitude1) {
        double dLat = Math.toRadians(latitude1 - latitude);
        double dLon = Math.toRadians(longitude1 - longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(latitude1)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c <= 20;
    }

    public Set<String> generateOfflinePaymentCodes(Wallet wallet) {
        if(wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Insufficient balance");

        Set<String> offline_codes = new HashSet<>();
        for(int i=0; i<5; i++) {
            String code = UUID.randomUUID().toString();
            offline_codes.add(code);
        }
        return offline_codes;
    }

}
