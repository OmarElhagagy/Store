package com.example.demo.repositories;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {
    
    // Find all shipping records for a specific order
    List<Shipping> findByOrder(CustomerOrder order);
    
    // Find by tracking number
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
    
    // Find by shipping provider
    List<Shipping> findByShippingProvider(String shippingProvider);
    
    // Find shipments shipped between dates
    List<Shipping> findByShippedDateBetween(Instant startDate, Instant endDate);
    
    // Find delivered shipments
    List<Shipping> findByDeliveredDateIsNotNull();
    
    // Find pending shipments (shipped but not delivered)
    List<Shipping> findByShippedDateIsNotNullAndDeliveredDateIsNull();
    
    // Find shipments by provider shipped after a certain date
    List<Shipping> findByShippingProviderAndShippedDateAfter(String provider, Instant date);
}