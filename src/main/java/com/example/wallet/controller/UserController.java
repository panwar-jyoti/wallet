package com.example.wallet.controller;

import com.example.wallet.exception.CustomException;
import com.example.wallet.service.AuthenticationService;
import com.example.wallet.service.UserService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public String registerUser(@RequestBody AuthenticationService.RegisterRequest req) {
        return authenticationService.registerUser(req);
    }

    @PostMapping("/authenticate")
    public String authenticateUser(@RequestBody AuthenticationService.AuthRequest req) {
        return authenticationService.authenticate(req);
    }
    @PostMapping("/enroll/{userId}")
    public String enrollForOfflinePayment(@PathVariable int userId) {
        userService.enrollForOfflinePayment(userId);
        return "Successfully enrolled";
    }

    @PostMapping("/add-funds-to-wallet/{userId}/{amount}")
    public String addFundsToWallet(@PathVariable int userId, @PathVariable long amount) {
        userService.addFundsToWallet(userId, amount);
        return "Successfully added to wallet";
    }


    @PostMapping("/allocate-offline-balance/{userId}/{amount}")
    public String transferMoneyToOffline(@PathVariable int userId, @PathVariable long amount) {
        userService.transferMoneyToOffline(userId, amount);
        return "Successfully transferred";
    }

    @PostMapping("/get-offline-codes/{userId}")
    public Set<String> getOfflineCodes(@PathVariable int userId) {
        return userService.getOfflineCodes(userId);
    }

    @PostMapping("/make-online-payment/{userId}/{vendorId}/{amount}/{otp}")
    public String makeOnlinePayment(@PathVariable int userId, @PathVariable int vendorId, @PathVariable long amount, @PathVariable String otp) {
        userService.makeOnlinePayment(userId, vendorId, amount, otp);
        return "Successfully done";
    }

    @PostMapping("/make-offline-payment/")
    public String makeOfflinePayment(@RequestBody OfflinePaymentRequest paymentRequest) {
        userService.makeOfflinePayment(paymentRequest.getUserId(), paymentRequest.getVendorId(), paymentRequest.getAmount(), paymentRequest.getLatitude(), paymentRequest.getLongitude(), paymentRequest.getCode());
        return "Offline payment done";
    }

    @GetMapping("/generateOTP/{userId}")
    public String generateOTP(@PathVariable int userId) {
        return userService.generateOTP(userId);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class OfflinePaymentRequest {
        int userId;
        int vendorId;
        long amount;
        double latitude;
        double longitude;
        String code;
    }
}