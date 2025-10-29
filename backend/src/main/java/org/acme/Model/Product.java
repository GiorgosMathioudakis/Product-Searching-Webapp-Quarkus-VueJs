package org.acme.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

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

    public Product(String name, String sku, BigDecimal price, String description) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.description = description;
    }


    @Column(nullable = false, length = 255)
    public String description;

    @CreationTimestamp
    @Column(nullable = false,name = "created_on" , columnDefinition = "TIMESTAMPTZ")
    public OffsetDateTime createdOn;

//    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime createdAt;

//    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
//    private OffsetDateTime updatedAt;

//    @PreUpdate
//    void preUpdate() {
//        this.updatedAt = OffsetDateTime.now();
//    }
}
