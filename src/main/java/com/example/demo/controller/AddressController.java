package com.example.demo.controller;

import com.example.demo.dto.AddressDTO;
import com.example.demo.entities.Address;
import com.example.demo.entities.Customer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address Controller", description = "API to manage customer addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private final CustomerService customerService;

    @Autowired
    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all addresses", description = "Returns a list of all addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        logger.info("Fetching all addresses");
        List<Address> addresses = addressService.getAllAddresses();
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(AddressDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDTOs);
    }

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
    }
}
