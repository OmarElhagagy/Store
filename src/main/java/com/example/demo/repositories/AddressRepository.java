package com.example.demo.repositories;

import com.example.demo.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByCustomerId(Integer customerId);
    List<Address> findByCustomerIdAndIdNot(Integer customerId, Integer addressId);
    Optional<Address> findByIdAndCustomerId(Integer addressId, Integer customerId);
    Optional<Address> findByCustomerIdAndIsDefaultTrue(Integer customerId);
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :customerId")
    void resetDefaultAddressForCustomer(@Param("customerId") Integer customerId);
}
