package com.example.demo.repositories;

import com.example.demo.entities.OrderDetail;
import com.example.demo.entities.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}