package com.example.demo.service.impl;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Storage;
import com.example.demo.entities.Store;
import com.example.demo.repositories.StorageRepository;
import com.example.demo.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    @Autowired
    public StorageServiceImpl(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @Override
    @Transactional
    public Storage createStorage(Store store, String location, Integer reorderLevel, 
                                Integer maxStock, Integer minStock) {
        Storage storage = new Storage();
        storage.setStore(store);
        storage.setLocation(location);
        storage.setReorderLevel(reorderLevel);
        storage.setMaxStock(maxStock);
        storage.setMinStock(minStock);
        storage.setEmployees(new HashSet<>());
        
        return storageRepository.save(storage);
    }

    @Override
    public Optional<Storage> findStorageById(Integer id) {
        return storageRepository.findById(id);
    }

    @Override
    public List<Storage> getAllStorageLocations() {
        return storageRepository.findAll();
    }

    @Override
    public List<Storage> getStorageByStore(Store store) {
        return storageRepository.findByStore(store);
    }

    @Override
    public List<Storage> getStorageByLocationKeyword(String locationKeyword) {
        return storageRepository.findByLocationContaining(locationKeyword);
    }

    @Override
    public List<Storage> getStorageByStoreAndLocationKeyword(Store store, String locationKeyword) {
        return storageRepository.findByStoreAndLocationContaining(store, locationKeyword);
    }

    @Override
    @Transactional
    public Storage updateStorage(Storage storage) {
        return storageRepository.save(storage);
    }

    @Override
    @Transactional
    public Storage updateStockLevels(Integer storageId, Integer reorderLevel, 
                                   Integer maxStock, Integer minStock) {
        Optional<Storage> optionalStorage = storageRepository.findById(storageId);
        
        if (optionalStorage.isPresent()) {
            Storage storage = optionalStorage.get();
            storage.setReorderLevel(reorderLevel);
            storage.setMaxStock(maxStock);
            storage.setMinStock(minStock);
            return storageRepository.save(storage);
        }
        
        throw new IllegalArgumentException("Storage not found with ID: " + storageId);
    }

    @Override
    @Transactional
    public void assignEmployeeToStorage(Storage storage, Employee employee) {
        employee.setStorageNo(storage);
        storage.getEmployees().add(employee);
        storageRepository.save(storage);
    }

    @Override
    @Transactional
    public void removeEmployeeFromStorage(Storage storage, Employee employee) {
        employee.setStorageNo(null);
        storage.getEmployees().remove(employee);
        storageRepository.save(storage);
    }

    @Override
    public Set<Employee> getEmployeesAssignedToStorage(Integer storageId) {
        Optional<Storage> optionalStorage = storageRepository.findById(storageId);
        
        return optionalStorage.map(Storage::getEmployees).orElse(new HashSet<>());
    }

    @Override
    public List<Storage> getStorageNeedingReorder() {
        return storageRepository.findStorageNeedingReorder();
    }

    @Override
    public List<Storage> getStorageAtCapacity() {
        return storageRepository.findStorageAtCapacity();
    }

    @Override
    public List<Storage> getStorageWithLowStock() {
        return storageRepository.findStorageWithLowStock();
    }

    @Override
    @Transactional
    public void deleteStorage(Integer storageId) {
        // Note: This will fail if there are employees assigned to this storage
        // due to foreign key constraints. You might want to handle this differently
        // based on your business requirements.
        storageRepository.deleteById(storageId);
    }
}