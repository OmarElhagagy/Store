package com.example.demo.repositories;

import com.example.demo.entities.ReservedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservedStockRepository extends JpaRepository<ReservedStock, Integer> {

    // Find active reservations for a product in a store
    @Query("SELECT rs FROM ReservedStock rs WHERE " +
           "rs.product.id = :productId AND " +
           "rs.store.id = :storeId AND " +
           "rs.expiresAt > CURRENT_TIMESTAMP")
    List<ReservedStock> findActiveReservations(@Param("productId") Integer productId, 
                                              @Param("storeId") Integer storeId);
    
    // Find all active reservations for a product across stores
    @Query("SELECT rs FROM ReservedStock rs WHERE " +
           "rs.product.id = :productId AND " +
           "rs.expiresAt > CURRENT_TIMESTAMP")
    List<ReservedStock> findActiveReservationsForProduct(@Param("productId") Integer productId);
    
    // Find expiring reservations
    @Query("SELECT rs FROM ReservedStock rs WHERE " +
           "rs.expiresAt BETWEEN CURRENT_TIMESTAMP AND :cutoff")
    List<ReservedStock> findExpiringReservations(@Param("cutoff") Instant cutoff);
    
    // Calculate total reserved quantity for a product in a store
    @Query("SELECT COALESCE(SUM(rs.quantity), 0) FROM ReservedStock rs WHERE " +
           "rs.product.id = :productId AND " +
           "rs.store.id = :storeId AND " +
           "rs.expiresAt > CURRENT_TIMESTAMP")
    int calculateReservedQuantity(@Param("productId") Integer productId, 
                                @Param("storeId") Integer storeId);
    
    // Delete expired reservations
    @Modifying
    @Query("DELETE FROM ReservedStock rs WHERE rs.expiresAt <= CURRENT_TIMESTAMP")
    void deleteExpiredReservations();
    
    // Extend reservation expiration
    @Modifying
    @Query("UPDATE ReservedStock rs SET rs.expiresAt = :newExpiresAt WHERE rs.id = :reservationId")
    void updateReservationExpiration(@Param("reservationId") Integer reservationId, 
                                   @Param("newExpiresAt") Instant newExpiresAt);
    
    // Update reservation quantity
    @Modifying
    @Query("UPDATE ReservedStock rs SET rs.quantity = :newQuantity WHERE rs.id = :reservationId")
    void updateReservationQuantity(@Param("reservationId") Integer reservationId, 
                                 @Param("newQuantity") Integer newQuantity);
}