package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.StoreRepository;
import com.example.demo.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public Store createStore(String address, String email, Integer minStock) {
        Store store = new Store();
        store.setAddress(address);
        store.setEmail(email);
        store.setMinStock(minStock);
        store.setEmployees(new HashSet<>());
        store.setNotifications(new HashSet<>());
        store.setReservedStocks(new HashSet<>());
        store.setStorages(new HashSet<>());
        store.setStoreInventories(new HashSet<>());
        
        return storeRepository.save(store);
    }

    @Override
    public Optional<Store> findStoreById(Integer id) {
        return storeRepository.findById(id);
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    public List<Store> getStoresByAddressKeyword(String addressKeyword) {
        return storeRepository.findByAddressContaining(addressKeyword);
    }

    @Override
    public List<Store> getStoresByEmailKeyword(String emailKeyword) {
        return storeRepository.findByEmailContaining(emailKeyword);
    }

    @Override
    @Transactional
    public Store updateStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    @Transactional
    public Store updateStoreContactInfo(Integer storeId, String address, String email) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            store.setAddress(address);
            store.setEmail(email);
            return storeRepository.save(store);
        }
        
        throw new IllegalArgumentException("Store not found with ID: " + storeId);
    }

    @Override
    @Transactional
    public Store updateStoreMinStock(Integer storeId, Integer minStock) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            store.setMinStock(minStock);
            return storeRepository.save(store);
        }
        
        throw new IllegalArgumentException("Store not found with ID: " + storeId);
    }

    @Override
    public Set<Employee> getStoreEmployees(Integer storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        return optionalStore.map(Store::getEmployees).orElse(new HashSet<>());
    }

    @Override
    public Set<Storage> getStoreStorageLocations(Integer storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        return optionalStore.map(Store::getStorages).orElse(new HashSet<>());
    }

    @Override
    public Set<StoreInventory> getStoreInventory(Integer storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        return optionalStore.map(Store::getStoreInventories).orElse(new HashSet<>());
    }

    @Override
    public Set<Notification> getStoreNotifications(Integer storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        return optionalStore.map(Store::getNotifications).orElse(new HashSet<>());
    }

    @Override
    public Set<ReservedStock> getStoreReservedStock(Integer storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        
        return optionalStore.map(Store::getReservedStocks).orElse(new HashSet<>());
    }

    @Override
    public List<Store> getStoresWithLowInventory() {
        return storeRepository.findStoresWithLowInventory();
    }

    @Override
    public List<Store> getStoresWithStorageLocations() {
        return storeRepository.findStoresWithStorageLocations();
    }

    @Override
    public List<Store> getStoresWithNoEmployees() {
        return storeRepository.findStoresWithNoEmployees();
    }

    @Override
    public List<Store> getStoresWithReservedStock() {
        return storeRepository.findStoresWithReservedStock();
    }

    @Override
    public List<Store> getStoresWithUnreadNotifications() {
        return storeRepository.findStoresWithUnreadNotifications();
    }

    @Override
    @Transactional
    public void deleteStore(Integer storeId) {
        // Note: This will fail if there are related entities due to
        // foreign key constraints. You might want to implement a more
        // sophisticated deletion strategy based on your requirements.
        storeRepository.deleteById(storeId);
    }
}