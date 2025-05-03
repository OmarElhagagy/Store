package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "\"Store_Inventory\"")
public class StoreInventory {
    private StoreInventoryId id;

    private Store store;

    private Product product;

    private Integer quantity;

    @EmbeddedId
    public StoreInventoryId getId() {
        return id;
    }

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Store_ID\"", nullable = false)
    public Store getStore() {
        return store;
    }

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Product_ID\"", nullable = false)
    public Product getProduct() {
        return product;
    }

    @NotNull
    @Column(name = "\"Quantity\"", nullable = false)
    public Integer getQuantity() {
        return quantity;
    }

}