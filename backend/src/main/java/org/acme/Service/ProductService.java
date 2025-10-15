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
    SessionFactory sessionFactory;

//    @Transactional
//    public boolean updateProduct(Long id, Product updatedProduct){
//
//        Product existingProduct = em.find(Product.class, id);
//
//        if(existingProduct != null){
//
//            existingProduct.name = updatedProduct.name;
//            existingProduct.sku = updatedProduct.sku;
//
////            em.merge(existingProduct);
//
//            return true;
//        }
//
//        return false;
//    }

    @Transactional
    public void createProduct(Product product){

        try (StatelessSession session = sessionFactory.openStatelessSession()) {

            session.insert(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert product", e);
        } finally {
            log.info("finally block");
        }
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
