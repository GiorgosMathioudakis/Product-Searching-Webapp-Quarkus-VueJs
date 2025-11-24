package org.acme.Service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.DTO.ProductPage;
import org.acme.Model.Product;
import org.hibernate.StatelessSession;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProductService {

    @Inject
    StatelessSession statelessSession;

    @Transactional
    public boolean updateProduct(Long id, Product updatedProduct){

        Product existingProduct = statelessSession.get(Product.class, id);

        if(existingProduct == null) return false;

//        if( getProductBySku(updatedProduct.sku) != existingProduct ) return false;

        existingProduct.name = updatedProduct.name;
        existingProduct.sku = updatedProduct.sku;
        existingProduct.price = updatedProduct.price;
        existingProduct.description = updatedProduct.description;

        statelessSession.update(existingProduct);

        return true;
    }

    private Product getProductBySku(String sku) {
        String query = "SELECT * FROM product where sku = :sku";

        return statelessSession.createNativeQuery(query ,
                Product.class).
                setParameter("sku", sku).
                getSingleResult();
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

    public ProductPage fetchProductPage(int pageNo, int pageSize, String name, String sku, String sortBy, String sortDir) {

        Set<String> allowedSorts = Set.of("updated_on", "created_on", "price", "name");
        String safeSortBy = allowedSorts.contains(sortBy) ? sortBy : "updated_on";

        int offset = (pageNo-1)*pageSize;

        String n = (name == null || name.isBlank()) ? "" : name.trim();

        String s = (sku  == null || sku.isBlank())  ? "" : sku.trim();

        String query = "SELECT * FROM product " +
                "WHERE " +
                "( name ILIKE ('%' || :n || '%') AND sku ILIKE ('%' || :s || '%') ) ";
        if (sortDir.equals("ASC")) {
            query += "ORDER BY " + safeSortBy + " ASC LIMIT :limit OFFSET :offset";
        } else if (sortDir.equals("DESC")) {
            query += "ORDER BY " + safeSortBy + " DESC LIMIT :limit OFFSET :offset";
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
