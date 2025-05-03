package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Product\"")
public class Product {
    private Integer id;

    private Supplier supplier;

    private String productName;

    private String size;

    private String brand;

    private BigDecimal price;

    private String color;

    private LocalDate launchDate;

    private String description;

    private Boolean isActive = false;

    private Set<Cart> carts = new LinkedHashSet<>();

    private Set<Image> images = new LinkedHashSet<>();

    private Set<Notification> notifications = new LinkedHashSet<>();

    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    private Set<PriceHistory> priceHistories = new LinkedHashSet<>();

    private Set<Category> categories = new LinkedHashSet<>();

    private Set<ReservedStock> reservedStocks = new LinkedHashSet<>();

    private Set<Return> returnFields = new LinkedHashSet<>();

    private Set<Review> reviews = new LinkedHashSet<>();

    private Set<StoreInventory> storeInventories = new LinkedHashSet<>();

    private Set<SupplyOrder> supplyOrders = new LinkedHashSet<>();

    private Set<Wishlist> wishlists = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Product_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Supplier_ID\"")
    public Supplier getSupplier() {
        return supplier;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "\"Product_Name\"", nullable = false, length = 100)
    public String getProductName() {
        return productName;
    }

    @Size(max = 10)
    @NotNull
    @Column(name = "\"Size\"", nullable = false, length = 10)
    public String getSize() {
        return size;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Brand\"", nullable = false, length = 50)
    public String getBrand() {
        return brand;
    }

    @NotNull
    @Column(name = "\"Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    @Size(max = 30)
    @NotNull
    @Column(name = "\"Color\"", nullable = false, length = 30)
    public String getColor() {
        return color;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Launch_Date\"", nullable = false)
    public LocalDate getLaunchDate() {
        return launchDate;
    }

    @Column(name = "\"Description\"", length = Integer.MAX_VALUE)
    public String getDescription() {
        return description;
    }

    @NotNull
    @ColumnDefault("true")
    @Column(name = "\"Is_Active\"", nullable = false)
    public Boolean getIsActive() {
        return isActive;
    }

    @OneToMany(mappedBy = "product")
    public Set<Cart> getCarts() {
        return carts;
    }

    @OneToMany(mappedBy = "product")
    public Set<Image> getImages() {
        return images;
    }

    @OneToMany(mappedBy = "product")
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = "product")
    public Set<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    @OneToMany(mappedBy = "product")
    public Set<PriceHistory> getPriceHistories() {
        return priceHistories;
    }

    @ManyToMany
    public Set<Category> getCategories() {
        return categories;
    }

    @OneToMany(mappedBy = "product")
    public Set<ReservedStock> getReservedStocks() {
        return reservedStocks;
    }

    @OneToMany(mappedBy = "product")
    public Set<Return> getReturnFields() {
        return returnFields;
    }

    @OneToMany(mappedBy = "product")
    public Set<Review> getReviews() {
        return reviews;
    }

    @OneToMany(mappedBy = "product")
    public Set<StoreInventory> getStoreInventories() {
        return storeInventories;
    }

    @OneToMany(mappedBy = "product")
    public Set<SupplyOrder> getSupplyOrders() {
        return supplyOrders;
    }

    @OneToMany(mappedBy = "product")
    public Set<Wishlist> getWishlists() {
        return wishlists;
    }

}