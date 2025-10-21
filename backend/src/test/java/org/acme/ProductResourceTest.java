package org.acme;


import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.Model.Product;
import org.acme.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import javax.ws.rs.core.Response;


import static org.junit.jupiter.api.Assertions.*;
import static org.wildfly.common.Assert.assertNotNull;


@QuarkusTest
public class ProductResourceTest {

    @InjectMock ProductService productService;

    @Inject ProductResource productResource;

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();
        product.name = "shirt";
        product.sku = "CH1";
    }

    @Test
    void getAllProducts(){
        List<Product> products = productService.findAllProducts();
        products.add(product);
        Mockito.when(productService.findAllProducts()).thenReturn(products);

        jakarta.ws.rs.core.Response response = productResource.getAllProducts();
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        List<Product> entity = (List<Product>) response.getEntity();
        assertFalse(entity.isEmpty());
        assertEquals("shirt" , entity.get(0).name);
        assertEquals("CH1" , entity.get(0).sku);
    }

    @Test
    void getProductsPageTest(){
        List<Product> products = productService.getPage(1,2);

    }

}
