package org.acme.Service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.acme.Model.Product;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    StatelessSession statelessSession;

    @Transactional
    public boolean updateProduct(Long id, Product updatedProduct){

        Product existingProduct = statelessSession.get(Product.class, id);

        if(existingProduct != null){

            existingProduct.name = updatedProduct.name;
            existingProduct.sku = updatedProduct.sku;

            statelessSession.update(existingProduct);

            return true;
        }

        return false;
    }

    @Transactional
    public void createProduct(Product product){

        Product new_p =  new Product();

        new_p.name=product.name;
        new_p.sku=product.sku;

        statelessSession.insert(new_p);
    }

//    @Transactional
//    public boolean deleteProduct(Long id) {
//
//        Product product = em.find(Product.class, id);
//
//        if (product != null) {
//            em.remove(product);
//            return true;
//        }
//
//        return false;
//
//    }
//
//    public List<Product> findAllProducts() {
//        return em.createQuery("SELECT p FROM Product p", Product.class)
//                .getResultList();
//    }
}
