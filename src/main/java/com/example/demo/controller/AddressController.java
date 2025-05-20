package com.example.demo.controller;

import com.example.demo.dto.AddressDTO;
import com.example.demo.entities.Address;
import com.example.demo.entities.Customer;
<<<<<<< HEAD
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.AddressService;
import com.example.demo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
import com.example.demo.service.AddressService;
import com.example.demo.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address Controller", description = "API to manage customer addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

=======
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing customer addresses
 */
@RestController
@RequestMapping("/api/addresses")
@Slf4j
public class AddressController {

>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    private final AddressService addressService;
    private final CustomerService customerService;

    @Autowired
    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

<<<<<<< HEAD
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all addresses", description = "Returns a list of all addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        logger.info("Fetching all addresses");
        List<Address> addresses = addressService.getAllAddresses();
=======
    /**
     * Get all addresses
     * Restricted to admin users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        log.info("REST request to get all Addresses");
        List<Address> addresses = addressService.findAll();
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(AddressDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDTOs);
    }

<<<<<<< HEAD
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isAddressAuthorized(authentication, #id)")
    @Operation(summary = "Get address by ID", description = "Returns a single address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address found",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<AddressDTO> getAddressById(
            @Parameter(description = "Address ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching address with ID: {}", id);
        
        Address address = addressService.findAddressById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        return ResponseEntity.ok(AddressDTO.fromEntity(address));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isCustomerAuthorized(authentication, #customerId)")
    @Operation(summary = "Get addresses by customer", description = "Returns addresses for a specific customer")
    public ResponseEntity<List<AddressDTO>> getAddressesByCustomer(@PathVariable Integer customerId) {
        logger.info("Fetching addresses for customer ID: {}", customerId);
        
        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<Address> addresses = addressService.getAddressesByCustomer(customer);
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(AddressDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(addressDTOs);
    }

    @PostMapping
    @Operation(summary = "Create new address", description = "Creates a new address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        logger.info("Creating new address for customer ID: {}", addressDTO.getCustomerId());
        
        Customer customer = customerService.findCustomerById(addressDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", addressDTO.getCustomerId()));
        
        Address address = addressService.createAddress(
                customer,
                addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getState(),
                addressDTO.getPostalCode(),
                addressDTO.getCountry(),
                addressDTO.getAddressType(),
                addressDTO.getIsDefault()
        );
        
        return new ResponseEntity<>(AddressDTO.fromEntity(address), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isAddressAuthorized(authentication, #id)")
    @Operation(summary = "Update address", description = "Updates an existing address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody AddressDTO addressDTO) {
        logger.info("Updating address with ID: {}", id);
        
        Address existingAddress = addressService.findAddressById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        // Update address fields
        existingAddress.setStreet(addressDTO.getStreet());
        existingAddress.setCity(addressDTO.getCity());
        existingAddress.setState(addressDTO.getState());
        existingAddress.setPostalCode(addressDTO.getPostalCode());
        existingAddress.setCountry(addressDTO.getCountry());
        existingAddress.setAddressType(addressDTO.getAddressType());
        
        // Only update default status if provided
        if (addressDTO.getIsDefault() != null) {
            existingAddress.setIsDefault(addressDTO.getIsDefault());
            
            // If setting as default, unset other addresses as default
            if (addressDTO.getIsDefault()) {
                addressService.setAsDefaultAddress(existingAddress);
            }
        }
        
        Address updatedAddress = addressService.updateAddress(existingAddress);
        return ResponseEntity.ok(AddressDTO.fromEntity(updatedAddress));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isAddressAuthorized(authentication, #id)")
    @Operation(summary = "Delete address", description = "Deletes an address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting address with ID: {}", id);
        
        Address address = addressService.findAddressById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isAddressAuthorized(authentication, #id)")
    @Operation(summary = "Set address as default", description = "Sets an address as the default for a customer")
    public ResponseEntity<AddressDTO> setAddressAsDefault(@PathVariable Integer id) {
        logger.info("Setting address ID: {} as default", id);
        
        Address address = addressService.findAddressById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        address = addressService.setAsDefaultAddress(address);
        return ResponseEntity.ok(AddressDTO.fromEntity(address));
=======
    /**
     * Get all addresses for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<AddressDTO>> getAddressesByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get all Addresses for customer ID {}", customerId);
        List<Address> addresses = addressService.findAddressByCustomerId(customerId);
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(AddressDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDTOs);
    }

    /**
     * Get default address for a customer
     */
    @GetMapping("/customer/{customerId}/default")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<AddressDTO> getDefaultAddress(@PathVariable Integer customerId) {
        log.info("REST request to get default Address for customer ID {}", customerId);
        Optional<Address> defaultAddress = addressService.getDefaultAddressForCustomer(customerId);
        return defaultAddress
                .map(address -> ResponseEntity.ok(AddressDTO.fromEntity(address)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get a specific address by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAddressResourceOwner(#id)")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Integer id) {
        log.info("REST request to get Address with ID {}", id);
        Optional<Address> address = addressService.findById(id);
        return address
                .map(value -> ResponseEntity.ok(AddressDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new address
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#addressDTO.customerId)")
    public ResponseEntity<?> createAddress(@Valid @RequestBody AddressDTO addressDTO, BindingResult bindingResult) {
        log.info("REST request to create Address for customer ID {}", addressDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Create new address entity
            Address address = new Address();
            
            // Set customer relationship
            Customer customer = customerService.findById(addressDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID " + addressDTO.getCustomerId()));
            address.setCustomer(customer);
            
            // Update the address with DTO values
            addressDTO.updateEntity(address);
            
            // Save the address
            Address savedAddress = addressService.createAddress(address);
            
            // Return the saved address as DTO
            return new ResponseEntity<>(AddressDTO.fromEntity(savedAddress), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating address", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating address: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing address
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAddressResourceOwner(#id)")
    public ResponseEntity<?> updateAddress(@PathVariable Integer id, 
                                          @Valid @RequestBody AddressDTO addressDTO,
                                          BindingResult bindingResult) {
        log.info("REST request to update Address with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Find existing address
            Address existingAddress = addressService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with ID " + id));
            
            // Check if customer ID matches
            if (!existingAddress.getCustomer().getId().equals(addressDTO.getCustomerId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change address ownership");
            }
            
            // Update address fields
            addressDTO.updateEntity(existingAddress);
            
            // Save updated address
            Address updatedAddress = addressService.updateAddress(existingAddress);
            
            return ResponseEntity.ok(AddressDTO.fromEntity(updatedAddress));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating address", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating address: " + e.getMessage(), e);
        }
    }

    /**
     * Delete an address
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAddressResourceOwner(#id)")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        log.info("REST request to delete Address with ID {}", id);
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting address", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting address: " + e.getMessage(), e);
        }
    }

    /**
     * Set an address as the default for a customer
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAddressResourceOwner(#id)")
    public ResponseEntity<AddressDTO> setDefaultAddress(@PathVariable Integer id, @RequestParam Integer customerId) {
        log.info("REST request to set Address ID {} as default for customer ID {}", id, customerId);
        try {
            Address defaultAddress = addressService.setDefaultAddress(id, customerId);
            return ResponseEntity.ok(AddressDTO.fromEntity(defaultAddress));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error setting default address", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error setting default address: " + e.getMessage(), e);
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    }
}
