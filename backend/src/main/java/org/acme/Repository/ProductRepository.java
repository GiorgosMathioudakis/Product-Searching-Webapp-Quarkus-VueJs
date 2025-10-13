package org.acme.Repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.Model.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Long> {

    public void saveProduct(Product product) {
        persist(product);
    }
}
