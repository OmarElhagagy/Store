package com.example.demo.repositories;

import com.example.demo.entities.ReservedStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedStockRepository extends JpaRepository<ReservedStock, Integer> {
}