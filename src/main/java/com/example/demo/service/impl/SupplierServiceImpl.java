package com.example.demo.service.impl;

import com.example.demo.entities.PhoneNumbersSupplier;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.SupplyOrder;
import com.example.demo.repositories.SupplierRepository;
import com.example.demo.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public Supplier createSupplier(String supplierName, String email) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierName);
        supplier.setEmail(email);
        supplier.setPhoneNumbersSuppliers(new HashSet<>());
        supplier.setProducts(new HashSet<>());
        supplier.setSupplyOrders(new HashSet<>());
        
        return supplierRepository.save(supplier);
    }

    @Override
    public Optional<Supplier> findSupplierById(Integer id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public List<Supplier> getSuppliersByNameKeyword(String supplierNameKeyword) {
        return supplierRepository.findBySupplierNameContaining(supplierNameKeyword);
    }

    @Override
    public List<Supplier> getSuppliersByEmailKeyword(String emailKeyword) {
        return supplierRepository.findByEmailContaining(emailKeyword);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplierInfo(Integer supplierId, String supplierName, String email) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        
        if (optionalSupplier.isPresent()) {
            Supplier supplier = optionalSupplier.get();
            supplier.setSupplierName(supplierName);
            supplier.setEmail(email);
            return supplierRepository.save(supplier);
        }
        
        throw new IllegalArgumentException("Supplier not found with ID: " + supplierId);
    }

    @Override
    public Set<PhoneNumbersSupplier> getSupplierPhoneNumbers(Integer supplierId) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        
        return optionalSupplier.map(Supplier::getPhoneNumbersSuppliers).orElse(new HashSet<>());
    }

    @Override
    public Set<Product> getSupplierProducts(Integer supplierId) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        
        return optionalSupplier.map(Supplier::getProducts).orElse(new HashSet<>());
    }

    @Override
    public Set<SupplyOrder> getSupplierOrders(Integer supplierId) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        
        return optionalSupplier.map(Supplier::getSupplyOrders).orElse(new HashSet<>());
    }

    @Override
    public List<Supplier> getSuppliersWithProducts() {
        return supplierRepository.findSuppliersWithProducts();
    }

    @Override
    public List<Supplier> getSuppliersWithActiveOrders() {
        return supplierRepository.findSuppliersWithActiveOrders();
    }

    @Override
    public List<Supplier> getSuppliersWithoutPhoneNumbers() {
        return supplierRepository.findSuppliersWithoutPhoneNumbers();
    }

    @Override
    public List<Supplier> getSuppliersWithSupplyOrders() {
        return supplierRepository.findSuppliersWithSupplyOrders();
    }

    @Override
    public long getSupplierCountByNamePattern(String supplierNameKeyword) {
        return supplierRepository.countBySupplierNameContaining(supplierNameKeyword);
    }

    @Override
    @Transactional
    public void deleteSupplier(Integer supplierId) {
        // Note: This will fail if there are related entities due to
        // foreign key constraints. You might want to implement a more
        // sophisticated deletion strategy based on your requirements.
        supplierRepository.deleteById(supplierId);
    }
}