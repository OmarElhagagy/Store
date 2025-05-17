package com.example.demo.service.impl;

import com.example.demo.entities.PhoneNumbersSupplier;
import com.example.demo.entities.Supplier;
import com.example.demo.repositories.PhoneNumbersSupplierRepository;
import com.example.demo.repositories.SupplierRepository;
import com.example.demo.service.PhoneNumbersSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneNumbersSupplierServiceImpl implements PhoneNumbersSupplierService {

    private final PhoneNumbersSupplierRepository phoneNumbersSupplierRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public PhoneNumbersSupplier addPhoneNumber(Integer supplierId, String phoneNumber) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        if (supplierOpt.isEmpty()) {
            return null;
        }

        if (phoneNumbersSupplierRepository.existsByPhoneNumber(phoneNumber)) {
            return null;
        }

        if (phoneNumbersSupplierRepository.findByPhoneNumberAndSupplierId(phoneNumber, supplierId).isPresent()) {
            return null;
        }

        PhoneNumbersSupplier newPhoneNumber = new PhoneNumbersSupplier();
        newPhoneNumber.setPhoneNumber(phoneNumber);
        newPhoneNumber.setSupplier(supplierOpt.get());

        return phoneNumbersSupplierRepository.save(newPhoneNumber);
    }

    @Override
    public List<PhoneNumbersSupplier> getPhoneNumbersBySupplier(Integer supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            return List.of();
        }
        return phoneNumbersSupplierRepository.findBySupplierId(supplierId);
    }

    @Override
    @Transactional
    public void deletePhoneNumber(String phoneNumber, Integer supplierId) {
        if (phoneNumbersSupplierRepository.existsByPhoneNumberAndSupplierId(phoneNumber, supplierId)) {
            phoneNumbersSupplierRepository.deleteByPhoneNumberAndSupplierId(phoneNumber, supplierId);
        }
    }

    @Override
    @Transactional
    public void deleteAllPhoneNumbersForSupplier(Integer supplierId) {
        if (supplierRepository.existsById(supplierId)) {
            phoneNumbersSupplierRepository.deleteAllBySupplierId(supplierId);
        }
    }

    @Override
    public boolean phoneNumberExists(String phoneNumber) {
        return phoneNumbersSupplierRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public List<PhoneNumbersSupplier> searchPhoneNumbers(String searchTerm) {
        return phoneNumbersSupplierRepository.searchByPhoneNumber(searchTerm);
    }

    @Override
    public PhoneNumbersSupplier getPhoneNumber(String phoneNumber, Integer supplierId) {
        return phoneNumbersSupplierRepository.findByPhoneNumberAndSupplierId(phoneNumber, supplierId)
                .orElse(null);
    }

    @Override
    public boolean phoneNumberExistsForOtherSuppliers(String phoneNumber, Integer supplierId) {
        return phoneNumbersSupplierRepository.existsByPhoneNumberForOtherSuppliers(phoneNumber, supplierId);
    }
}