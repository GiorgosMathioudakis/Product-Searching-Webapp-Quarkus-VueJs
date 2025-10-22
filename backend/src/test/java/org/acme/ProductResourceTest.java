package org.acme;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.Model.Product;
import org.acme.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductResourceTest {

    @Inject
    ProductService productService;

    @BeforeEach
    @TestTransaction
    void setup() {
        // Clean and seed data before each test
        List<Product> existing = productService.findAllProducts();
        if (!existing.isEmpty()) {
            existing.forEach(p -> productService.deleteProductById(p.id));
        }

        Product p1 = new Product();
        p1.name = "Hat";
        p1.sku = "GR1";
        productService.createProduct(p1);

        Product p2 = new Product();
        p2.name = "Shirt";
        p2.sku = "CH1";
        productService.createProduct(p2);
    }

    @Test
    void testGetAllProductsEndpoint() {
        given()
                .when().get("/products")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$", hasSize(2))
                    .body("[0].name", notNullValue())
                    .body("[0].sku", notNullValue())
                    .body("[*].name", hasItems("Hat", "Shirt"));


    }

}
