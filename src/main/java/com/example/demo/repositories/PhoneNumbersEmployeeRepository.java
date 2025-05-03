package com.example.demo.repositories;

import com.example.demo.entities.PhoneNumbersEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumbersEmployeeRepository extends JpaRepository<PhoneNumbersEmployee, String> {
}