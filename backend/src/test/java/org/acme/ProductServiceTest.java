package org.acme;


import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.Model.Product;
import org.acme.Service.ProductService;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ProductServiceTest {

    @Inject
    ProductService productService;

    @InjectMock
    StatelessSession statelessSession;

    private Product product;
    private NativeQuery<Product> mockQuery;

    @BeforeEach
    void setup() {
        product = new Product();
        product.name = "shirt";
        product.sku = "CH1";

        mockQuery = Mockito.mock(NativeQuery.class);
    }

    @Test
    void createProduct_returnsTrueWhenPersisted() {
        Mockito.when(statelessSession.insert(ArgumentMatchers.any(Product.class))).thenReturn(1L);

        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(product);

        boolean result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    void createProduct_returnsFalseWhenPersisted() {
        Mockito.when(statelessSession.insert(ArgumentMatchers.any(Product.class))).thenReturn(1L);

        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(null);

        boolean result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(false, result);
    }

    @Test
    void givenGetAll_returnsAllProducts() {

        List<Product> products = new ArrayList<>();
        products.add(product);

        Mockito.when(statelessSession.createNativeQuery(ArgumentMatchers.anyString()))
                .thenReturn(mockQuery);

        Mockito.when(mockQuery.getResultList())
                .thenReturn(products);

        List<Product> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(products, result);
        assertEquals(1, result.size());
        assertEquals("shirt", result.get(0).name);
        assertEquals("CH1", result.get(0).sku);
    }

}
