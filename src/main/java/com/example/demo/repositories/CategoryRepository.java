package com.example.demo.repositories;

import com.example.demo.entities.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String name);
    List<Category> findByCategoryNameContainingAndIgnoreCase(String keyword);
    boolean existsByCategoryName(String name);
    List<Category> findByProductIsNotEmpty();
}
