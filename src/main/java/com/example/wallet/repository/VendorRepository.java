package com.example.wallet.repository;

import com.example.wallet.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Boolean existsByUsername(String username);
}
