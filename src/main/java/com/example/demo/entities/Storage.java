package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Storage\"")
public class Storage {
    private Integer id;

    private Store store;

    private String location;

    private Integer reorderLevel;

    private Integer maxStock;

    private Integer minStock;

    private Set<Employee> employees = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Storage_No\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Store_ID\"", nullable = false)
    public Store getStore() {
        return store;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "\"Location\"", nullable = false, length = 100)
    public String getLocation() {
        return location;
    }

    @NotNull
    @Column(name = "\"Reorder_Level\"", nullable = false)
    public Integer getReorderLevel() {
        return reorderLevel;
    }

    @NotNull
    @Column(name = "\"Max_Stock\"", nullable = false)
    public Integer getMaxStock() {
        return maxStock;
    }

    @NotNull
    @Column(name = "\"Min_Stock\"", nullable = false)
    public Integer getMinStock() {
        return minStock;
    }

    @OneToMany(mappedBy = "storageNo")
    public Set<Employee> getEmployees() {
        return employees;
    }

}