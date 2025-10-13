package org.acme.Service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.Repository.ProductRepository;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    public void saveProduct(org.acme.Model.Product product) {
        productRepository.saveProduct(product);
    }
}
