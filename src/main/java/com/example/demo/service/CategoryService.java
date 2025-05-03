package com.example.demo.service;

import com.example.demo.entities.Category;

import java.util.Optional;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Integer id);
    Optional<Category> findByCategoryName(String name);
    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);
    boolean existsByCategoryName(String name);
    boolean existsById(Integer id);
    List<Category> findByProductsIsNotEmpty();
    Category save(Category category);
    void deleteById(Integer id);
    long count();
    List<Category> saveAll(List<Category> categories);
}
