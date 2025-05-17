package com.example.demo.service.impl;

import com.example.demo.entities.Image;
import com.example.demo.entities.Product;
import com.example.demo.repositories.ImageRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Image addImage(Integer productId, String imageUrl, boolean isPrimary) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return null;
        }

        if (imageRepository.existsByImageUrl(imageUrl)) {
            return null;
        }

        if (isPrimary) {
            imageRepository.resetPrimaryImages(productId);
        }

        Image newImage = new Image();
        newImage.setProduct(productOpt.get());
        newImage.setImageUrl(imageUrl);
        newImage.setPrimary(isPrimary);

        return imageRepository.save(newImage);
    }

    @Override
    public List<Image> getImagesByProduct(Integer productId) {
        return productRepository.existsById(productId) ? 
               imageRepository.findByProductId(productId) : 
               List.of();
    }

    @Override
    public Image getPrimaryImage(Integer productId) {
        return productRepository.existsById(productId) ?
               imageRepository.findPrimaryByProductId(productId).orElse(null) :
               null;
    }

    @Override
    @Transactional
    public void deleteImage(Integer imageId) {
        imageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void deleteAllImagesForProduct(Integer productId) {
        if (productRepository.existsById(productId)) {
            imageRepository.deleteAllByProductId(productId);
        }
    }

    @Override
    @Transactional
    public Image setAsPrimary(Integer imageId) {
        Optional<Image> imageOpt = imageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            return null;
        }

        Image image = imageOpt.get();
        imageRepository.resetPrimaryImages(image.getProduct().getId());
        image.setPrimary(true);
        
        return imageRepository.save(image);
    }

    @Override
    public boolean imageUrlExists(String imageUrl) {
        return imageRepository.existsByImageUrl(imageUrl);
    }
}