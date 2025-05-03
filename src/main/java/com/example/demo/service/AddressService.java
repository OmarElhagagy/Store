package com.example.demo.service;

import com.example.demo.entities.Address;
import java.util.List;
import java.util.Optional;

public interface AddressService {
    // Basic CRUD operations
    Address createAddress(Address address);
    Address updateAddress(Address address);
    void deleteAddress(Integer id);
    Optional<Address> findById(Integer id);
    List<Address> findAll();

    // Customer related operations
    List<Address> findAddressByCustomerId(Integer customerId);

    // Default address operation
    Address setDefaultAddress(Integer addressId, Integer customerId);
    Optional<Address> getDefaultAddressForCustomer(Integer customerId);

    // Address validation
    boolean isValidAddress(Address address);
}
