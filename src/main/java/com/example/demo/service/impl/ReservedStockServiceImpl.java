package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ReservedStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservedStockServiceImpl implements ReservedStockService {

    private final ReservedStockRepository reservedStockRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    @Override
    @Transactional
    public ReservedStock createReservation(Integer storeId, Integer productId, Integer quantity, Instant expiresAt) {
        // Validate store and product exist
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        Optional<Product> productOpt = productRepository.findById(productId);
        if (storeOpt.isEmpty() || productOpt.isEmpty()) {
            return null;
        }

        // Check available inventory
        Optional<StoreInventory> inventoryOpt = storeInventoryRepository
            .findByStoreIdAndProductId(storeId, productId);
        
        if (inventoryOpt.isEmpty()) {
            return null;
        }

        StoreInventory inventory = inventoryOpt.get();
        int reservedQuantity = reservedStockRepository
            .calculateReservedQuantity(productId, storeId);
        int available = inventory.getQuantity() - reservedQuantity;

        if (quantity <= 0 || quantity > available) {
            return null;
        }

        // Create reservation
        ReservedStock reservation = new ReservedStock();
        reservation.setStore(storeOpt.get());
        reservation.setProduct(productOpt.get());
        reservation.setQuantity(quantity);
        reservation.setReservedAt(Instant.now());
        reservation.setExpiresAt(expiresAt);

        return reservedStockRepository.save(reservation);
    }

    @Override
    public Optional<ReservedStock> getReservation(Integer reservationId) {
        return reservedStockRepository.findById(reservationId);
    }

    @Override
    public List<ReservedStock> getActiveReservations(Integer productId, Integer storeId) {
        if (!productRepository.existsById(productId) || !storeRepository.existsById(storeId)) {
            return List.of();
        }
        return reservedStockRepository.findActiveReservations(productId, storeId);
    }

    @Override
    public List<ReservedStock> getActiveReservationsForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return reservedStockRepository.findActiveReservationsForProduct(productId);
    }

    @Override
    public List<ReservedStock> getExpiringReservations(Instant cutoff) {
        return reservedStockRepository.findExpiringReservations(cutoff);
    }

    @Override
    public int getReservedQuantity(Integer productId, Integer storeId) {
        if (!productRepository.existsById(productId) || !storeRepository.existsById(storeId)) {
            return 0;
        }
        return reservedStockRepository.calculateReservedQuantity(productId, storeId);
    }

    @Override
    @Transactional
    public ReservedStock updateReservationQuantity(Integer reservationId, Integer newQuantity) {
        Optional<ReservedStock> reservationOpt = reservedStockRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            return null;
        }

        ReservedStock reservation = reservationOpt.get();
        int productId = reservation.getProduct().getId();
        int storeId = reservation.getStore().getId();

        // Check inventory
        Optional<StoreInventory> inventoryOpt = storeInventoryRepository
            .findByStoreIdAndProductId(storeId, productId);
        
        if (inventoryOpt.isEmpty()) {
            return null;
        }

        StoreInventory inventory = inventoryOpt.get();
        int reservedQuantity = reservedStockRepository
            .calculateReservedQuantity(productId, storeId) - reservation.getQuantity();
        int available = inventory.getQuantity() - reservedQuantity;

        if (newQuantity <= 0 || newQuantity > available) {
            return null;
        }

        reservedStockRepository.updateReservationQuantity(reservationId, newQuantity);
        return reservedStockRepository.findById(reservationId).orElse(null);
    }

    @Override
    @Transactional
    public ReservedStock extendReservation(Integer reservationId, Instant newExpiresAt) {
        if (newExpiresAt.isBefore(Instant.now())) {
            return null;
        }

        reservedStockRepository.updateReservationExpiration(reservationId, newExpiresAt);
        return reservedStockRepository.findById(reservationId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteReservation(Integer reservationId) {
        reservedStockRepository.deleteById(reservationId);
    }

    @Override
    @Transactional
    public void cleanupExpiredReservations() {
        reservedStockRepository.deleteExpiredReservations();
    }
}