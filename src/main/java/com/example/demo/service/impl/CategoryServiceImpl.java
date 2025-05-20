package com.example.demo.service.impl;

import com.example.demo.service.CategoryService;

import jakarta.persistence.EntityNotFoundException;

import com.example.demo.entities.Category;
import com.example.demo.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByCategoryName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findByCategoryNameContainingIgnoreCase(String keyword) {
        return categoryRepository.findByCategoryNameContainingAndIgnoreCase(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(String name) {
        return categoryRepository.existsByCategoryName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findByProductsIsNotEmpty() {
        return categoryRepository.findByProductIsNotEmpty();
    }

    @Override
    public List<Category> saveAll(List<Category> categories) {
        // validate all categories before saving
        for (Category category : categories) {
            if(category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }
        }
        return categoryRepository.saveAll(categories);
    }

    @Override
    public void deleteById(Integer id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found with Id" + id));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category that has products");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category save(Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        // check for duplicates when creating new category
        if (category.getId() == null && categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findSubcategoriesByParentId(Integer parentId) {
        // Check if parent category exists
        if (!existsById(parentId)) {
            throw new EntityNotFoundException("Parent category not found with ID: " + parentId);
        }
        
        return categoryRepository.findByParentCategoryId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> findTopLevelCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }
    
    @Override
    @Transactional
    public Category createCategory(Category category) {
        // This is an alias for save() but with additional validations specific to creation
        if (category.getId() != null) {
            throw new IllegalArgumentException("New category should not have an ID");
        }
        
        return save(category);
    }
    
    @Override
    @Transactional
    public Category updateCategory(Category category) {
        // This is an alias for save() but with validations specific to updates
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID is required for updates");
        }
        
        // Check that the category exists
        if (!existsById(category.getId())) {
            throw new EntityNotFoundException("Category not found with ID: " + category.getId());
        }
        
        return save(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        // This is an alias for deleteById() for controller compatibility
        deleteById(id);
    }
}
