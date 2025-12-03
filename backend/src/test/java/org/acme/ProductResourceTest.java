package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.Service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductResourceTest {

    @Inject
    ProductService productService;

    @Inject
    DataSource dataSource;

    private void clearDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            // TRUNCATE: Wipes data fast
            // RESTART IDENTITY: Resets ID counter back to 1 (Important for your ID tests)
            // CASCADE: Removes data from other tables that link to this one
            stmt.execute("TRUNCATE TABLE product RESTART IDENTITY CASCADE");
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        // 1. Clean the DB first (To be absolutely sure)
        clearDatabase();

        // 2. Insert Test Data
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("INSERT INTO product (name, sku, price, description, created_on, updated_on) " +
                    "VALUES ('Hat', 'GR1', 1.00, 'description', '2025-11-05 09:24:50.641386+00', '2025-11-05 09:24:50.641386+00')");

            stmt.execute("INSERT INTO product (name, sku, price, description, created_on, updated_on) " +
                    "VALUES ('Shirt', 'CH1', 2.00, 'description', '2025-10-29 13:32:46.480509+00', '2025-10-29 13:32:46.480509+00')");
        }
    }

    @AfterEach
    void cleanup() throws SQLException {
        // 3. Clean up after the test finishes
        clearDatabase();
    }

    @Test
    void testPostProductEndpoint() {
        given()
            .contentType(ContentType.JSON)
                .body("{\"name\":\"Hat\",\"price\":10.00,\"sku\":\"GR2\",\"description\": \"Updated description\"}")
            .when().post("/products")
                .then()
                .statusCode(201)
                .body("id", nullValue())
                .body("name", equalTo("Hat"))
                .body("sku", equalTo("GR2"));
    }

    @Test
    void testPutProductEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body("{\"name\":\"Boot\",\"sku\":\"RO1\", \"price\": 55.00, \"description\": \"Updated description\"}")
                .when().put("/products/{id}")
                .then()
                .statusCode(200)
                .body("id", nullValue())
                .body("name", equalTo("Boot"))
                .body("sku", equalTo("RO1"));
    }

    @Test
    void testDeleteProductEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .when().delete("/products/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void testDeleteProductEndpointKO() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 3)
                .when().delete("/products/{id}")
                .then()
                .statusCode(404);

    }

    @Test
    void testGetProductsEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("pageNo", 1)
                .queryParam("pageSize", 1)
                .queryParam("name" , "hat")
                .queryParam("sku", "GR1")
                .queryParam("sortDir" , "ASC")
                .queryParam("sortBy" , "price")
                .when().get("/products")
                .then()
                .body("products[0].name", equalTo("Hat"))
                .body("products[0].sku", equalTo("GR1"))
                .body("hasNext", equalTo(false));
    }

}
