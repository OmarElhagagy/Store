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
    
    // Additional methods required by CategoryController
    
    // Find subcategories of a parent category
    List<Category> findSubcategoriesByParentId(Integer parentId);
    
    // Find top-level categories (categories without a parent)
    List<Category> findTopLevelCategories();
    
    // Create a new category
    Category createCategory(Category category);
    
    // Update an existing category
    Category updateCategory(Category category);
    
    // Delete a category - different signature from deleteById for compatibility
    void deleteCategory(Integer id);
}
