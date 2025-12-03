package org.acme.Service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.DTO.ProductPage;
import org.acme.Model.Product;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Set;

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

    public boolean isSkuTaken(String sku, Long idToIgnore){
        String sql = "SELECT * FROM product where sku = :sku ";

        //update
        if(idToIgnore != null){
            sql += " AND id != :idToIgnore ";
        }

        var query = statelessSession.createNativeQuery(sql, Product.class)
                .setParameter("sku", sku);

        if(idToIgnore != null){
            query.setParameter("idToIgnore", idToIgnore);
        }

        return !query.getResultList().isEmpty();

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

        boolean isSearchMode = !n.isEmpty() || !s.isEmpty();

        String sql;

        if (isSearchMode) {

            sql = "WITH matches AS MATERIALIZED ( " +
                    "   SELECT id FROM product " +
                    "   WHERE ( name ILIKE ('%' || :n || '%') AND sku ILIKE ('%' || :s || '%') ) " +
                    ") " +
                    "SELECT p.* FROM product p " +
                    "JOIN matches m ON p.id = m.id ";
        } else {

            sql = "SELECT p.* FROM product p " +
                    "WHERE 1=1 ";
        }

        if ("ASC".equalsIgnoreCase(sortDir)) {
            sql += "ORDER BY p." + safeSortBy + " ASC ";
        } else {
            sql += "ORDER BY p." + safeSortBy + " DESC ";
        }

        sql += "LIMIT :limit OFFSET :offset";

        var query = statelessSession.createNativeQuery(sql, Product.class);

        if (isSearchMode) {
            query.setParameter("n", n);
            query.setParameter("s", s);
        }

        return new ProductPage(
                query.setParameter("limit", pageSize + 1)
                        .setParameter("offset", offset)
                        .getResultList(),
                pageSize
        );

    }
}
