package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.Service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductResourceTest {

    @Inject
    ProductService productService;

    @BeforeAll
    static void setup() throws Exception {

        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:productdb", "sa", "sa")) {

            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement()) {
                // Drop table if exists (to start clean)
                stmt.execute("DROP TABLE IF EXISTS product");

                // Recreate the schema manually
                stmt.execute("""
                CREATE TABLE product (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255),
                    sku VARCHAR(255)
                )
            """);

                // Insert seed data directly via SQL
                stmt.execute("INSERT INTO product (name, sku) VALUES ('Hat', 'GR1')");
                stmt.execute("INSERT INTO product (name, sku) VALUES ('Shirt', 'CH1')");
            }

            connection.commit();
        }
    }


    @AfterEach
    void cleanup() {
        // TODO: clean up test products (jdbc)

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
                    .body("name", hasItems("Hat", "Shirt"));
    }

    @Test
    void testPostProductEndpoint() {
        given()
            .contentType(ContentType.JSON)
                .body("{\"name\":\"Hat\",\"sku\":\"GR1\"}")
            .when().post("/products")
                .then()
                .statusCode(201)
                .body("id", nullValue())
                .body("name", equalTo("Hat"))
                .body("sku", equalTo("GR1"));
    }

    @Test
    void testPutProductEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body("{\"name\":\"Boot\",\"sku\":\"RO1\"}")
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

}
