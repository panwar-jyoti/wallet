package com.example.wallet.service;

import com.example.wallet.model.Vendor;
import org.springframework.http.ResponseEntity;


public interface VendorService {

    public String registerVendor(Vendor vendor);
}
