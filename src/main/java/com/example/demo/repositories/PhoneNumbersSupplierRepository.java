package com.example.demo.repositories;

import com.example.demo.entities.PhoneNumbersSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumbersSupplierRepository extends JpaRepository<PhoneNumbersSupplier, String> {
}