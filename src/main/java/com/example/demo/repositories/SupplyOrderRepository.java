package com.example.demo.repositories;

import com.example.demo.entities.SupplyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Integer> {
}