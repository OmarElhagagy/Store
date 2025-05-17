package com.example.demo.service;

import com.example.demo.entities.ReservedStock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservedStockService {

    // Create a new reservation
    ReservedStock createReservation(Integer storeId, Integer productId, Integer quantity, Instant expiresAt);
    
    // Get reservation by ID
    Optional<ReservedStock> getReservation(Integer reservationId);
    
    // Get active reservations for product in store
    List<ReservedStock> getActiveReservations(Integer productId, Integer storeId);
    
    // Get all active reservations for product
    List<ReservedStock> getActiveReservationsForProduct(Integer productId);
    
    // Get expiring reservations
    List<ReservedStock> getExpiringReservations(Instant cutoff);
    
    // Calculate reserved quantity
    int getReservedQuantity(Integer productId, Integer storeId);
    
    // Update reservation
    ReservedStock updateReservationQuantity(Integer reservationId, Integer newQuantity);
    ReservedStock extendReservation(Integer reservationId, Instant newExpiresAt);
    
    // Delete reservation
    void deleteReservation(Integer reservationId);
    
    // Maintenance
    void cleanupExpiredReservations();
}