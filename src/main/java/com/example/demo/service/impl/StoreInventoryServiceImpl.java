package com.example.demo.service.impl;

import com.example.demo.entities.Product;
import com.example.demo.entities.Store;
import com.example.demo.entities.StoreInventory;
import com.example.demo.entities.StoreInventoryId;
import com.example.demo.repositories.StoreInventoryRepository;
import com.example.demo.service.StoreInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StoreInventoryServiceImpl implements StoreInventoryService {

    private final StoreInventoryRepository storeInventoryRepository;

    @Autowired
    public StoreInventoryServiceImpl(StoreInventoryRepository storeInventoryRepository) {
        this.storeInventoryRepository = storeInventoryRepository;
    }

    @Override
    @Transactional
    public StoreInventory createInventory(Store store, Product product, Integer quantity) {
        StoreInventoryId id = new StoreInventoryId(store.getId(), product.getId());
        
        Optional<StoreInventory> existingInventory = storeInventoryRepository.findById(id);
        if (existingInventory.isPresent()) {
            throw new IllegalStateException("Inventory already exists for store " + store.getId() + 
                                           " and product " + product.getId());
        }
        
        StoreInventory inventory = new StoreInventory();
        inventory.setId(id);
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        
        return storeInventoryRepository.save(inventory);
    }

    @Override
    public Optional<StoreInventory> findInventoryById(StoreInventoryId id) {
        return storeInventoryRepository.findById(id);
    }

    @Override
    public Optional<StoreInventory> findInventoryByStoreAndProduct(Store store, Product product) {
        return storeInventoryRepository.findByStoreAndProduct(store, product);
    }

    @Override
    public List<StoreInventory> getAllInventoryItems() {
        return storeInventoryRepository.findAll();
    }

    @Override
    public List<StoreInventory> getInventoryByStore(Store store) {
        return storeInventoryRepository.findByStore(store);
    }

    @Override
    public List<StoreInventory> getInventoryByProduct(Product product) {
        return storeInventoryRepository.findByProduct(product);
    }

    @Override
    @Transactional
    public StoreInventory updateInventoryQuantity(Store store, Product product, Integer newQuantity) {
        Optional<StoreInventory> optionalInventory = storeInventoryRepository.findByStoreAndProduct(store, product);
        
        if (optionalInventory.isPresent()) {
            StoreInventory inventory = optionalInventory.get();
            inventory.setQuantity(newQuantity);
            return storeInventoryRepository.save(inventory);
        }
        
        throw new IllegalArgumentException("Inventory not found for store " + store.getId() + 
                                         " and product " + product.getId());
    }

    @Override
    @Transactional
    public StoreInventory incrementInventoryQuantity(Store store, Product product, Integer incrementAmount) {
        Optional<StoreInventory> optionalInventory = storeInventoryRepository.findByStoreAndProduct(store, product);
        
        if (optionalInventory.isPresent()) {
            StoreInventory inventory = optionalInventory.get();
            inventory.setQuantity(inventory.getQuantity() + incrementAmount);
            return storeInventoryRepository.save(inventory);
        }
        
        throw new IllegalArgumentException("Inventory not found for store " + store.getId() + 
                                         " and product " + product.getId());
    }

    @Override
    @Transactional
    public StoreInventory decrementInventoryQuantity(Store store, Product product, Integer decrementAmount) {
        Optional<StoreInventory> optionalInventory = storeInventoryRepository.findByStoreAndProduct(store, product);
        
        if (optionalInventory.isPresent()) {
            StoreInventory inventory = optionalInventory.get();
            int newQuantity = inventory.getQuantity() - decrementAmount;
            
            // Prevent negative quantities
            if (newQuantity < 0) {
                throw new IllegalStateException("Cannot decrement by " + decrementAmount + 
                                              " as it would result in negative quantity. Current quantity: " + 
                                              inventory.getQuantity());
            }
            
            inventory.setQuantity(newQuantity);
            return storeInventoryRepository.save(inventory);
        }
        
        throw new IllegalArgumentException("Inventory not found for store " + store.getId() + 
                                         " and product " + product.getId());
    }

    @Override
    public List<StoreInventory> getLowStockItems() {
        return storeInventoryRepository.findLowStockItems();
    }

    @Override
    public List<StoreInventory> getLowStockItemsByStore(Store store) {
        return storeInventoryRepository.findLowStockItemsByStore(store);
    }

    @Override
    public List<StoreInventory> getOutOfStockItems() {
        return storeInventoryRepository.findByQuantity(0);
    }

    @Override
    public List<StoreInventory> getItemsWithQuantityLessThanEqual(Integer maxQuantity) {
        return storeInventoryRepository.findByQuantityLessThanEqual(maxQuantity);
    }

    @Override
    public List<StoreInventory> getItemsWithQuantityGreaterThanEqual(Integer minQuantity) {
        return storeInventoryRepository.findByQuantityGreaterThanEqual(minQuantity);
    }

    @Override
    @Transactional
    public void deleteInventory(StoreInventoryId id) {
        storeInventoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteInventoryByStoreAndProduct(Store store, Product product) {
        Optional<StoreInventory> optionalInventory = storeInventoryRepository.findByStoreAndProduct(store, product);
        
        if (optionalInventory.isPresent()) {
            storeInventoryRepository.delete(optionalInventory.get());
        } else {
            throw new IllegalArgumentException("Inventory not found for store " + store.getId() + 
                                             " and product " + product.getId());
        }
    }
}