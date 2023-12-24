package com.example.wallet.controller;

import com.example.wallet.exception.CustomException;
import com.example.wallet.model.Admin;
import com.example.wallet.model.Vendor;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.AdminRepository;
import com.example.wallet.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/register")
    public void registerVendor(@RequestBody Vendor vendor) {
        if (vendorRepository.existsById(vendor.getVendorId())) {
            throw new CustomException(HttpStatus.ALREADY_REPORTED, "Vendor exists");
        }
        vendor.setRegistrationStatus(Vendor.RegistrationStatus.PENDING);
        vendorRepository.save(vendor);
    }

    @PostMapping("/approve/{adminId}")
    public String approveVendor(
            @PathVariable int adminId,
            @PathVariable int vendorId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Wallet storeWallet = vendor.getStoreWallet();
        if (storeWallet == null) {
            storeWallet = new Wallet();
            vendor.setStoreWallet(storeWallet);
        }


        vendor.setRegistrationStatus(Vendor.RegistrationStatus.APPROVED);
        vendorRepository.save(vendor);

        return "Vendor approved successfully.";
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
