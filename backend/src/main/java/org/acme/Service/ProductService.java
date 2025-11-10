package org.acme.Service;


import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.DTO.ProductPage;
import org.acme.Model.Product;
import org.hibernate.StatelessSession;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    StatelessSession statelessSession;

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

    public ProductPage fetchProducts(int pageNo, int pageSize, String name, String sku, String sortBy, String sortDir) {

        int offset = (pageNo-1)*pageSize;

        String n = (name == null || name.isBlank()) ? "" : name.trim();

        String s = (sku  == null || sku.isBlank())  ? "" : sku.trim();

        String query = "SELECT * FROM product " +
                "WHERE " +
                "( UPPER(name) LIKE UPPER('%' || :n || '%') AND UPPER(sku) LIKE UPPER('%' || :s || '%') ) ";
        if (sortDir.equals("ASC")) {
            query += "ORDER BY " + sortBy + " ASC LIMIT :limit OFFSET :offset";
        } else if (sortDir.equals("DESC")) {
            query += "ORDER BY " + sortBy + " DESC LIMIT :limit OFFSET :offset";
        } else {
            throw new IllegalArgumentException();
        }

        List<Product> productList = statelessSession.createNativeQuery(
                query   , Product.class)
                .setParameter("n" , n)
                .setParameter("s" , s)
                .setParameter("limit", pageSize+1)
                .setParameter("offset", offset)
                .getResultList();

        return ( new ProductPage(productList, pageSize) );

    }
}
