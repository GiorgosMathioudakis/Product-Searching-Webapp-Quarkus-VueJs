package org.acme.Repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.Model.Product;


@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {



}
