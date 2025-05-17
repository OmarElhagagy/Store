package com.example.demo.service.impl;


import com.example.demo.entities.Employee;
import com.example.demo.entities.PhoneNumbersEmployee;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.PhoneNumbersEmployeeRepository;
import com.example.demo.service.PhoneNumbersEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneNumbersEmployeeServiceImpl implements PhoneNumbersEmployeeService {

    private final PhoneNumbersEmployeeRepository phoneNumbersEmployeeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public PhoneNumbersEmployee addPhoneNumber(Integer employeeId, String phoneNumber) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return null;
        }

        if (phoneNumbersEmployeeRepository.existsByPhoneNumber(phoneNumber)) {
            return null;
        }

        if (phoneNumbersEmployeeRepository.findByPhoneNumberAndEmployeeId(phoneNumber, employeeId).isPresent()) {
            return null;
        }

        PhoneNumbersEmployee newPhoneNumber = new PhoneNumbersEmployee();
        newPhoneNumber.setPhoneNumber(phoneNumber);
        newPhoneNumber.setEmployee(employeeOpt.get());

        return phoneNumbersEmployeeRepository.save(newPhoneNumber);
    }

    @Override
    public List<PhoneNumbersEmployee> getPhoneNumbersByEmployee(Integer employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return Collections.emptyList();
        }
        return phoneNumbersEmployeeRepository.findByEmployeeId(employeeId);
    }

@Override
@Transactional
public void deletePhoneNumber(String phoneNumber, Integer employeeId) {
    if (phoneNumbersEmployeeRepository.existsByPhoneNumberAndEmployeeId(phoneNumber, employeeId)) {
        phoneNumbersEmployeeRepository.deleteByPhoneNumberAndEmployeeId(phoneNumber, employeeId);
    }
}

    @Override
    @Transactional
    public void deleteAllPhoneNumbersForEmployee(Integer employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            phoneNumbersEmployeeRepository.deleteAllByEmployeeId(employeeId);
        }
    }

    @Override
    public boolean phoneNumberExists(String phoneNumber) {
        return phoneNumbersEmployeeRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public List<PhoneNumbersEmployee> searchPhoneNumbers(String searchTerm) {
        return phoneNumbersEmployeeRepository.searchByPhoneNumber(searchTerm);
    }

    @Override
    public PhoneNumbersEmployee getPhoneNumber(String phoneNumber, Integer employeeId) {
        return phoneNumbersEmployeeRepository.findByPhoneNumberAndEmployeeId(phoneNumber, employeeId)
                .orElse(null);
    }

    @Override
    public boolean phoneNumberExistsForOtherEmployees(String phoneNumber, Integer employeeId) {
        return phoneNumbersEmployeeRepository.existsByPhoneNumberForOtherEmployees(phoneNumber, employeeId);
    }
}