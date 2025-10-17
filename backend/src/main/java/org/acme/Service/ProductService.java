package org.acme.Service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.acme.Model.Product;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    EntityManager em;

    @Transactional
    public boolean updateProduct(Long id, Product updatedProduct){

        Product existingProduct = em.find(Product.class, id);

        if(existingProduct != null){

            existingProduct.name = updatedProduct.name;
            existingProduct.sku = updatedProduct.sku;
            
            return true;
        }

        return false;
    }

    @Transactional
    public void createProduct(Product product){
        Product new_p = new Product();

        new_p.name = product.name;
        new_p.sku = product.sku;

        em.persist(product);

    }

    @Transactional
    public boolean deleteProduct(Long id) {

        Product product = em.find(Product.class, id);

        if (product != null) {
            em.remove(product);
            return true;
        }

        return false;

    }

    public List<Product> findAllProducts() {
        return em.createQuery("SELECT p FROM Product p", Product.class)
                .getResultList();
    }
}
