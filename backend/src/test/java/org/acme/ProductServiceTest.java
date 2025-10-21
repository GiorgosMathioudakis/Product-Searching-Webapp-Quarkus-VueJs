package org.acme;


import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.Model.Product;
import org.acme.Service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class ProductServiceTest {

    @Inject
    ProductService productService;

    @Test
    public void testFindAllProducts() {
        Assertions.assertEquals("[]" , productService.findAllProducts());
    }

}
