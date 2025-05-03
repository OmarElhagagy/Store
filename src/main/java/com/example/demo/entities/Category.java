package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Categories\"")
public class Category {
    private Integer id;

    private String categoryName;

    private Set<Product> products = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Category_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Category_Name\"", nullable = false, length = 50)
    public String getCategoryName() {
        return categoryName;
    }

    @ManyToMany
    @JoinTable(name = "Product_Categories",
            joinColumns = @JoinColumn(name = "Category_ID"),
            inverseJoinColumns = @JoinColumn(name = "Product_ID"))
    public Set<Product> getProducts() {
        return products;
    }

}