package com.example.demo.repositories;

import com.example.demo.entities.StoreInventory;
import com.example.demo.entities.StoreInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, StoreInventoryId> {
}