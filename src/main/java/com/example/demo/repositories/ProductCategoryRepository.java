package com.example.demo.repositories;

import com.example.demo.entities.ProductCategory;
import com.example.demo.entities.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
}