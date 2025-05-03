package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductCategoryId implements Serializable {
    private static final long serialVersionUID = 1101921439419120209L;
    private Integer productId;

    private Integer categoryId;

    @NotNull
    @Column(name = "\"Product_ID\"", nullable = false)
    public Integer getProductId() {
        return productId;
    }

    @NotNull
    @Column(name = "\"Category_ID\"", nullable = false)
    public Integer getCategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductCategoryId entity = (ProductCategoryId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }

}