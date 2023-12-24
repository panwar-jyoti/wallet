package com.example.wallet.serviceImpl;

import com.example.wallet.config.SecurityConfig;
import com.example.wallet.exception.CustomException;
import com.example.wallet.model.Vendor;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.VendorRepository;
import com.example.wallet.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class VendorServiceImpl implements VendorService {
    @Autowired
    VendorRepository vendorRepository;
    @Autowired
    private SecurityConfig.JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public String registerVendor(Vendor vendor) {
        if(vendorRepository.existsByUsername(vendor.getUsername())) {

        }
        vendor.setRegistrationStatus(Vendor.RegistrationStatus.PENDING);
        vendorRepository.save(vendor);
        var jwtToken = jwtService.generateToken(vendor);
        return jwtToken;
    }
}
