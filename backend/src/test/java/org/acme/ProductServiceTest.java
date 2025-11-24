package org.acme;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.DTO.ProductPage;
import org.acme.Model.Product;
import org.acme.Service.ProductService;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Mockito.when(statelessSession.insert(ArgumentMatchers.any(Product.class))).thenReturn(product);

        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(product);

        boolean result = productService.createProduct(product);

        assertTrue(result);
    }

    @Test
    void createProduct_returnsFalseWhenPersisted() {
        Mockito.when(statelessSession.insert(ArgumentMatchers.any(Product.class))).thenReturn(product);

        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(null);

        boolean result = productService.createProduct(product);

        assertFalse(result);
    }

    @Test
    void deleteByIdOK() {
        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(product);

        boolean response = productService.deleteProductById(1L);

        assertTrue(response);
    }

    @Test
    void deleteByIdKO() {
        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(null);

        boolean response = productService.deleteProductById(1L);

        assertFalse(response);
    }

    @Test
    void updateProduct_returnsTrueWhenUpdated() {
        Product updatedProduct = new Product();
        updatedProduct.name = "updated product";
        updatedProduct.sku = "GR1";

        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.anyLong())).thenReturn(product);

        boolean response = productService.updateProduct(1L,updatedProduct);

        assertTrue(response);

        assertEquals("updated product", product.name);
        assertEquals("GR1", product.sku);
        Mockito.verify(statelessSession).update(product);

    }

    @Test
    void updateProduct_returnsFalseWhenNotFound() {
        Mockito.when(statelessSession.get(ArgumentMatchers.eq(Product.class), ArgumentMatchers.any())).thenReturn(null);

        Mockito.doNothing().when(statelessSession).update(ArgumentMatchers.any(Product.class));

        boolean response = productService.updateProduct(1L,product);

        assertFalse(response);
    }

    @Test
    void getProductsTest(){
        List<Product> products = new ArrayList<>();

        products.add(new Product("Hat","GR1",new BigDecimal(10),""));
        products.add(new Product("Shirt","CH1",new BigDecimal(12),""));

        Mockito.when(statelessSession.createNativeQuery(
                Mockito.anyString(),
                Mockito.eq(Product.class))
        ).thenReturn(mockQuery);


        Mockito.when(mockQuery.setParameter(Mockito.anyString(), Mockito.any()))
                .thenReturn(mockQuery);

        Mockito.when(mockQuery.getResultList())
                .thenReturn(products.subList(1,2));

        ProductPage result = productService.fetchProductPage(2, 1,"Shi" , "CH" , "price" , "DESC");

        assertNotNull(result);
        assertEquals(1, result.products.size());
        assertEquals("Shirt", result.products.get(0).name);
        assertEquals("CH1", result.products.get(0).sku);
        assertEquals(false, result.hasNext);

    }


}
