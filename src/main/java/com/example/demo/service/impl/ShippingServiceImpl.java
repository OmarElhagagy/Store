package com.example.demo.service.impl;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Shipping;
import com.example.demo.repositories.ShippingRepository;
import com.example.demo.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;

    @Autowired
    public ShippingServiceImpl(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    @Override
    @Transactional
    public Shipping createShipping(CustomerOrder order, String trackingNumber, String shippingProvider) {
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setTrackingNumber(trackingNumber);
        shipping.setShippingProvider(shippingProvider);
        
        return shippingRepository.save(shipping);
    }

    @Override
    public Optional<Shipping> findShippingById(Integer id) {
        return shippingRepository.findById(id);
    }

    @Override
    public Optional<Shipping> findShippingByTrackingNumber(String trackingNumber) {
        return shippingRepository.findByTrackingNumber(trackingNumber);
    }

    @Override
    public List<Shipping> getShippingByOrder(CustomerOrder order) {
        return shippingRepository.findByOrder(order);
    }

    @Override
    @Transactional
    public Shipping updateShipping(Shipping shipping) {
        return shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public Shipping markAsShipped(Integer shippingId, Instant shippedDate) {
        Optional<Shipping> optionalShipping = shippingRepository.findById(shippingId);
        
        if (optionalShipping.isPresent()) {
            Shipping shipping = optionalShipping.get();
            shipping.setShippedDate(shippedDate);
            return shippingRepository.save(shipping);
        }
        
        throw new IllegalArgumentException("Shipping not found with ID: " + shippingId);
    }

    @Override
    @Transactional
    public Shipping markAsDelivered(Integer shippingId, Instant deliveredDate) {
        Optional<Shipping> optionalShipping = shippingRepository.findById(shippingId);
        
        if (optionalShipping.isPresent()) {
            Shipping shipping = optionalShipping.get();
            shipping.setDeliveredDate(deliveredDate);
            return shippingRepository.save(shipping);
        }
        
        throw new IllegalArgumentException("Shipping not found with ID: " + shippingId);
    }

    @Override
    public List<Shipping> getPendingShipments() {
        return shippingRepository.findByShippedDateIsNotNullAndDeliveredDateIsNull();
    }

    @Override
    public List<Shipping> getDeliveredShipments() {
        return shippingRepository.findByDeliveredDateIsNotNull();
    }

    @Override
    public List<Shipping> getShipmentsByProvider(String provider) {
        return shippingRepository.findByShippingProvider(provider);
    }

    @Override
    public List<Shipping> getShipmentsInDateRange(Instant startDate, Instant endDate) {
        return shippingRepository.findByShippedDateBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public void deleteShipping(Integer shippingId) {
        shippingRepository.deleteById(shippingId);
    }
}