package org.acme.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false,length = 255)
    public String name;

    @Column(nullable = false,unique = true, length = 12)
    public String sku;

//    @Column(nullable = false, precision = 12, scale = 2)
//    public BigDecimal price;
//
//    @Column(columnDefinition = "text", length = 255)
//    public String description;
//
//    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime updatedAt;
//
//    @PrePersist
//    void prePersist() {
//        OffsetDateTime now = OffsetDateTime.now();
//        this.createdAt = now;
//        this.updatedAt = now;
//    }
//
//    @PreUpdate
//    void preUpdate() {
//        this.updatedAt = OffsetDateTime.now();
//    }
}
