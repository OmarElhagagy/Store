package com.example.demo.service;

import com.example.demo.entities.Image;
import java.util.List;

public interface ImageService {

    // Add a new image for a product
    Image addImage(Integer productId, String imageUrl, boolean isPrimary);

    // Get all images for a product
    List<Image> getImagesByProduct(Integer productId);

    // Get primary image for a product
    Image getPrimaryImage(Integer productId);

    // Delete an image
    void deleteImage(Integer imageId);

    // Delete all images for a product
    void deleteAllImagesForProduct(Integer productId);

    // Set an image as primary
    Image setAsPrimary(Integer imageId);

    // Check if image URL exists
    boolean imageUrlExists(String imageUrl);
}