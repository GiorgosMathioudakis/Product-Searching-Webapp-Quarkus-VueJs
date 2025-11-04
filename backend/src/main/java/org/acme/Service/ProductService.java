package org.acme.Service;


import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.Model.Product;
import org.hibernate.StatelessSession;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    StatelessSession statelessSession;

    @Transactional
    public List<Product> findAllProductsByNameAndSku(String name,String sku){

        String n = (name == null || name.isBlank()) ? null : name.trim();

        String s = (sku  == null || sku.isBlank())  ? null : sku.trim();

        if(n == null && s == null) return findAllProducts();

        return statelessSession.createNativeQuery("select * from product " +
                "WHERE " +
                "( UPPER(name) LIKE UPPER('%' || :n || '%') AND sku LIKE UPPER('%' || :s || '%') )" , Product.class)
                .setParameter("n", n == null ? "" : n)
                .setParameter("s", s == null ? "" : s)
                .getResultList();

    }


    @Transactional
    public boolean updateProduct(Long id, Product updatedProduct){

        Product existingProduct = statelessSession.get(Product.class, id);

        if(existingProduct == null) return false;

        existingProduct.name = updatedProduct.name;
        existingProduct.sku = updatedProduct.sku;
        existingProduct.price = updatedProduct.price;
        existingProduct.description = updatedProduct.description;

        statelessSession.update(existingProduct);

        return true;
    }

    @Transactional
    public boolean createProduct(Product product){

        Product new_p =  new Product();

        new_p.name=product.name;
        new_p.sku=product.sku;
        new_p.price=product.price;
        new_p.description=product.description;

        statelessSession.insert(new_p);

        Product persistedProduct = statelessSession.get(Product.class, new_p.id);

        return persistedProduct != null;

    }

    @Transactional
    public boolean deleteProductById(Long id) {

        Product product = statelessSession.get(Product.class, id);

        if(product == null) return false;

        statelessSession.delete(product);

        return true;

    }

    @Transactional
    public List<Product> findAllProducts() {
        Log.info("Finding all products");
        return statelessSession.createNativeQuery("select * from product" , Product.class).getResultList();
    }

    @Transactional
    public List<Product> getPage(int pageNo, int limit) {

        int offset = (pageNo-1)*limit;

        return statelessSession.createNativeQuery("select * from Product LIMIT :limit OFFSET :offest " , Product.class)
                .setParameter("offest", offset)
                .setParameter("limit", limit)
                .getResultList();

    }

}
