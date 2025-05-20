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
public class StoreInventoryId implements Serializable {
    private static final long serialVersionUID = -391651820592838035L;
    private Integer storeId;

    private Integer productId;

    public StoreInventoryId(Integer id, Integer id2) {
        //TODO Auto-generated constructor stub
    }

    @NotNull
    @Column(name = "\"Store_ID\"", nullable = false)
    public Integer getStoreId() {
        return storeId;
    }

    @NotNull
    @Column(name = "\"Product_ID\"", nullable = false)
    public Integer getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StoreInventoryId entity = (StoreInventoryId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.storeId, entity.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, storeId);
    }

}