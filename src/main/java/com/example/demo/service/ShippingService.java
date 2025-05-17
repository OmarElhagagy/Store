package com.example.demo.service;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Shipping;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShippingService {
    
    // Create a new shipping record
    Shipping createShipping(CustomerOrder order, String trackingNumber, String shippingProvider);
    
    // Find shipping by ID
    Optional<Shipping> findShippingById(Integer id);
    
    // Find shipping by tracking number
    Optional<Shipping> findShippingByTrackingNumber(String trackingNumber);
    
    // Get all shipping records for an order
    List<Shipping> getShippingByOrder(CustomerOrder order);
    
    // Update shipping information
    Shipping updateShipping(Shipping shipping);
    
    // Mark shipping as shipped
    Shipping markAsShipped(Integer shippingId, Instant shippedDate);
    
    // Mark shipping as delivered
    Shipping markAsDelivered(Integer shippingId, Instant deliveredDate);
    
    // Get all pending shipments
    List<Shipping> getPendingShipments();
    
    // Get all delivered shipments
    List<Shipping> getDeliveredShipments();
    
    // Get shipments by provider
    List<Shipping> getShipmentsByProvider(String provider);
    
    // Get shipments in date range
    List<Shipping> getShipmentsInDateRange(Instant startDate, Instant endDate);
    
    // Delete shipping record
    void deleteShipping(Integer shippingId);
}