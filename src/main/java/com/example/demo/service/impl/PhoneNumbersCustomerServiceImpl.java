package com.example.demo.service.impl;

import com.example.demo.entities.Customer;
import com.example.demo.entities.PhoneNumbersCustomer;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.PhoneNumbersCustomerRepository;
import com.example.demo.service.PhoneNumbersCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneNumbersCustomerServiceImpl implements PhoneNumbersCustomerService {

    private final PhoneNumbersCustomerRepository phoneNumbersCustomerRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public PhoneNumbersCustomer addPhoneNumber(Integer customerId, String phoneNumber) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return null;
        }

        if (phoneNumbersCustomerRepository.existsByPhoneNumber(phoneNumber) || 
            phoneNumbersCustomerRepository.findByPhoneNumberAndCustomerId(phoneNumber, customerId).isPresent()) {
            return null;
        }

        PhoneNumbersCustomer newPhoneNumber = new PhoneNumbersCustomer();
        newPhoneNumber.setPhoneNumber(phoneNumber);
        newPhoneNumber.setCustomer(customerOpt.get());

        return phoneNumbersCustomerRepository.save(newPhoneNumber);
    }

    @Override
    public List<PhoneNumbersCustomer> getPhoneNumbersByCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            return Collections.emptyList();
        }
        return phoneNumbersCustomerRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public void deletePhoneNumber(String phoneNumber, Integer customerId) {
        if (phoneNumbersCustomerRepository.existsByPhoneNumberAndCustomerId(phoneNumber, customerId)) {
            phoneNumbersCustomerRepository.deleteByPhoneNumberAndCustomerId(phoneNumber, customerId);
        }
    }

    @Override
    @Transactional
    public void deleteAllPhoneNumbersForCustomer(Integer customerId) {
        if (customerRepository.existsById(customerId)) {
            phoneNumbersCustomerRepository.deleteAllByCustomerId(customerId);
        }
    }

    @Override
    public boolean phoneNumberExists(String phoneNumber) {
        return phoneNumbersCustomerRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public List<PhoneNumbersCustomer> searchPhoneNumbers(String searchTerm) {
        return phoneNumbersCustomerRepository.searchByPhoneNumber(searchTerm);
    }

    @Override
    public PhoneNumbersCustomer getPhoneNumber(String phoneNumber, Integer customerId) {
        return phoneNumbersCustomerRepository.findByPhoneNumberAndCustomerId(phoneNumber, customerId)
                .orElse(null);
    }
}