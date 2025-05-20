package com.example.demo.controller;

import com.example.demo.dto.ImageDTO;
import com.example.demo.entities.Image;
import com.example.demo.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing product images
 */
@RestController
@RequestMapping("/api/images")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Get all images for a product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageDTO>> getProductImages(@PathVariable Integer productId) {
        log.info("REST request to get Images for product ID {}", productId);
        List<Image> images = imageService.findByProductId(productId);
        List<ImageDTO> imageDTOs = images.stream()
                .map(image -> {
                    ImageDTO dto = ImageDTO.fromEntity(image);
                    // Set full URL for image access
                    String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/images/")
                            .path(image.getId().toString())
                            .path("/content")
                            .toUriString();
                    dto.setUrl(imageUrl);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(imageDTOs);
    }

    /**
     * Get image metadata by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable Integer id) {
        log.info("REST request to get Image metadata with ID {}", id);
        return imageService.findById(id)
                .map(image -> {
                    ImageDTO dto = ImageDTO.fromEntity(image);
                    // Set full URL for image access
                    String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/images/")
                            .path(image.getId().toString())
                            .path("/content")
                            .toUriString();
                    dto.setUrl(imageUrl);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get image content by ID
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getImageContent(@PathVariable Integer id) {
        log.info("REST request to get Image content with ID {}", id);
        try {
            Resource resource = imageService.loadImageAsResource(id);
            String contentType = imageService.getContentType(id).orElse("application/octet-stream");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error retrieving image content", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving image: " + e.getMessage(), e);
        }
    }

    /**
     * Upload a new image for a product
     * Restricted to admin and staff users
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") Integer productId,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "isPrimary", required = false, defaultValue = "false") Boolean isPrimary) {
        
        log.info("REST request to upload Image for product ID {}", productId);
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Only image files are allowed");
        }
        
        try {
            Image savedImage = imageService.storeImage(file, productId, altText, isPrimary);
            
            // Create URL for accessing the uploaded image
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/images/")
                    .path(savedImage.getId().toString())
                    .path("/content")
                    .toUriString();
            
            ImageDTO imageDTO = ImageDTO.fromEntity(savedImage);
            imageDTO.setUrl(imageUrl);
            
            return new ResponseEntity<>(imageDTO, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error uploading image", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading image: " + e.getMessage(), e);
        }
    }

    /**
     * Update image metadata
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> updateImage(
            @PathVariable Integer id,
            @Valid @RequestBody ImageDTO imageDTO,
            BindingResult bindingResult) {
        
        log.info("REST request to update Image metadata with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Image updatedImage = imageService.updateImageMetadata(id, imageDTO);
            
            // Create URL for accessing the image
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/images/")
                    .path(updatedImage.getId().toString())
                    .path("/content")
                    .toUriString();
            
            ImageDTO updatedDTO = ImageDTO.fromEntity(updatedImage);
            updatedDTO.setUrl(imageUrl);
            
            return ResponseEntity.ok(updatedDTO);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating image metadata", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating image: " + e.getMessage(), e);
        }
    }

    /**
     * Delete an image
     * Restricted to admin and staff users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        log.info("REST request to delete Image with ID {}", id);
        try {
            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error deleting image", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting image: " + e.getMessage(), e);
        }
    }

    /**
     * Set image as primary for a product
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/set-primary")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ImageDTO> setPrimaryImage(@PathVariable Integer id) {
        log.info("REST request to set Image ID {} as primary", id);
        try {
            Image image = imageService.setPrimaryImage(id);
            
            // Create URL for accessing the image
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/images/")
                    .path(image.getId().toString())
                    .path("/content")
                    .toUriString();
            
            ImageDTO imageDTO = ImageDTO.fromEntity(image);
            imageDTO.setUrl(imageUrl);
            
            return ResponseEntity.ok(imageDTO);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error setting primary image", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error setting primary image: " + e.getMessage(), e);
        }
    }

    /**
     * Reorder product images
     * Restricted to admin and staff users
     */
    @PutMapping("/product/{productId}/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ImageDTO>> reorderImages(
            @PathVariable Integer productId,
            @RequestBody List<Integer> imageIds) {
        log.info("REST request to reorder Images for product ID {}", productId);
        try {
            List<Image> images = imageService.reorderImages(productId, imageIds);
            
            List<ImageDTO> imageDTOs = images.stream()
                    .map(image -> {
                        ImageDTO dto = ImageDTO.fromEntity(image);
                        // Set full URL for image access
                        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/images/")
                                .path(image.getId().toString())
                                .path("/content")
                                .toUriString();
                        dto.setUrl(imageUrl);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(imageDTOs);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error reordering images", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reordering images: " + e.getMessage(), e);
        }
    }

    /**
     * Handle validation errors and return appropriate response
     */
    private ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
} 