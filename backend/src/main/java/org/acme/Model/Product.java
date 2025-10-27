package org.acme.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false,length = 255)
    public String name;

    @Column(nullable = false,unique = true, length = 12)
    public String sku;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal price;

    public Product(){}

    public Product(String name, String sku, BigDecimal price) {
        this.name = name;
        this.sku = sku;
        this.price = price;
    }

//    @Column(columnDefinition = "text", length = 255)
//    public String description;
//
//    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime updatedAt;
//2
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
