package com.example.wallet.controller;

import com.example.wallet.exception.CustomException;
import com.example.wallet.model.Admin;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.service.AdminService;
import freemarker.core.ReturnInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody Admin admin) {
        adminService.createAdmin(admin);
        return ResponseEntity.ok("Admin created");
    }
    @PostMapping("/approve-user/{adminId}/{userId}")
    public ResponseEntity<String> approveUser(@PathVariable int adminId, @PathVariable int userId) {
        adminService.approveUser(adminId, userId);
        return ResponseEntity.ok("User approved by admin");
    }
    @PostMapping("/approve-vendor/{adminId}/{vendorId}")
    public ResponseEntity<String> approveVendor(@PathVariable int vendorId, @PathVariable int adminId) {
        adminService.approveVendor(adminId, vendorId);
        return ResponseEntity.ok("Vendor approved by admin");
    }

    @GetMapping("/get-flagged-transactions")
    public List<WalletTransaction> getFlaggedTransactions() {
        return adminService.getFlaggedTransactions();
    }

    @PostMapping("/review-transaction/{adminId}/{transactionId}")
    public ResponseEntity<String> reviewTransaction(@PathVariable int adminId, @PathVariable int transactionId, @RequestParam Boolean approve) {
        adminService.reviewTransaction(adminId, transactionId, approve);
        return ResponseEntity.ok("Transaction reviewed by admin");
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
