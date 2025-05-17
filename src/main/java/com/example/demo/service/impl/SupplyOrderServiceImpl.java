package com.example.demo.service.impl;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.SupplyOrder;
import com.example.demo.repositories.SupplyOrderRepository;
import com.example.demo.service.SupplyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SupplyOrderServiceImpl implements SupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;

    @Autowired
    public SupplyOrderServiceImpl(SupplyOrderRepository supplyOrderRepository) {
        this.supplyOrderRepository = supplyOrderRepository;
    }

    @Override
    @Transactional
    public SupplyOrder createSupplyOrder(Supplier supplier, Product product, Employee employee, 
                                       Integer quantityOrdered, BigDecimal price) {
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setSupplier(supplier);
        supplyOrder.setProduct(product);
        supplyOrder.setEmployee(employee);
        supplyOrder.setQuantityOrdered(quantityOrdered);
        supplyOrder.setPrice(price);
        supplyOrder.setOrderDate(LocalDate.now());
        
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    public Optional<SupplyOrder> findSupplyOrderById(Integer id) {
        return supplyOrderRepository.findById(id);
    }

    @Override
    public List<SupplyOrder> getAllSupplyOrders() {
        return supplyOrderRepository.findAll();
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersBySupplier(Supplier supplier) {
        return supplyOrderRepository.findBySupplier(supplier);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByProduct(Product product) {
        return supplyOrderRepository.findByProduct(product);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByEmployee(Employee employee) {
        return supplyOrderRepository.findByEmployee(employee);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByDate(LocalDate date) {
        return supplyOrderRepository.findByOrderDate(date);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        return supplyOrderRepository.findByOrderDateBetween(startDate, endDate);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersWithMinimumQuantity(Integer minQuantity) {
        return supplyOrderRepository.findByQuantityOrderedGreaterThanEqual(minQuantity);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersWithMinimumPrice(BigDecimal minPrice) {
        return supplyOrderRepository.findByPriceGreaterThanEqual(minPrice);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersWithMaximumPrice(BigDecimal maxPrice) {
        return supplyOrderRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<SupplyOrder> getRecentSupplyOrders(int daysBack) {
        LocalDate date = LocalDate.now().minusDays(daysBack);
        return supplyOrderRepository.findRecentOrders(date);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersBySupplierAndProduct(Supplier supplier, Product product) {
        return supplyOrderRepository.findBySupplierAndProduct(supplier, product);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersBySupplierAndDateRange(Supplier supplier, 
                                                                LocalDate startDate, 
                                                                LocalDate endDate) {
        return supplyOrderRepository.findBySupplierAndOrderDateBetween(supplier, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalValueBySupplier(Supplier supplier) {
        BigDecimal totalValue = supplyOrderRepository.calculateTotalValueBySupplier(supplier);
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalValueByProduct(Product product) {
        BigDecimal totalValue = supplyOrderRepository.calculateTotalValueByProduct(product);
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public SupplyOrder updateSupplyOrder(SupplyOrder supplyOrder) {
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    @Transactional
    public SupplyOrder updateOrderDetails(Integer supplyOrderId, Integer quantityOrdered, BigDecimal price) {
        Optional<SupplyOrder> optionalOrder = supplyOrderRepository.findById(supplyOrderId);
        
        if (optionalOrder.isPresent()) {
            SupplyOrder order = optionalOrder.get();
            order.setQuantityOrdered(quantityOrdered);
            order.setPrice(price);
            return supplyOrderRepository.save(order);
        }
        
        throw new IllegalArgumentException("Supply Order not found with ID: " + supplyOrderId);
    }

    @Override
    @Transactional
    public SupplyOrder assignEmployeeToOrder(Integer supplyOrderId, Employee employee) {
        Optional<SupplyOrder> optionalOrder = supplyOrderRepository.findById(supplyOrderId);
        
        if (optionalOrder.isPresent()) {
            SupplyOrder order = optionalOrder.get();
            order.setEmployee(employee);
            return supplyOrderRepository.save(order);
        }
        
        throw new IllegalArgumentException("Supply Order not found with ID: " + supplyOrderId);
    }

    @Override
    @Transactional
    public void deleteSupplyOrder(Integer supplyOrderId) {
        supplyOrderRepository.deleteById(supplyOrderId);
    }
}