package com.example.demo.repositories;

import com.example.demo.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    // Find all images for a specific product
    List<Image> findByProductId(Integer productId);

    // Find primary image for a product
    @Query("SELECT i FROM Image i WHERE i.product.id = :productId AND i.isPrimary = true")
    Optional<Image> findPrimaryByProductId(@Param("productId") Integer productId);

    // Check if an image URL already exists
    boolean existsByImageUrl(String imageUrl);

    // Delete all images for a specific product
    @Modifying
    @Query("DELETE FROM Image i WHERE i.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Integer productId);

    // Set all images as non-primary for a product
    @Modifying
    @Query("UPDATE Image i SET i.isPrimary = false WHERE i.product.id = :productId")
    void resetPrimaryImages(@Param("productId") Integer productId);

    // Find image by URL and product
    Optional<Image> findByImageUrlAndProductId(String imageUrl, Integer productId);
}