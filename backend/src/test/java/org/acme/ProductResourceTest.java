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
                id BIGINT   auto_increment PRIMARY KEY,
                    name VARCHAR(255),
                    sku VARCHAR(255),
                    price numeric(12,2),
                    description VARCHAR(255),
                    created_on TIMESTAMP WITH TIME ZONE,
                    updated_on TIMESTAMP WITH TIME ZONE
                )
            """);

                // Insert seed data directly via SQL
                stmt.execute("INSERT INTO product (name, sku , price , description , created_on , updated_on) VALUES ('Hat', 'GR1' , '1' , 'description' , '2025-11-05 09:24:50.641386 +00:00' , '2025-11-05 09:24:50.641386 +00:00')");
                stmt.execute("INSERT INTO product (name, sku , price , description , created_on , updated_on) VALUES ('Shirt', 'CH1' , '2' , 'description' , '2025-10-29 13:32:46.480509 +00:00' , '2025-10-29 13:32:46.480509 +00:00')");
            }

            connection.commit();
        }
    }


    @AfterEach
    void cleanup() {
        // TODO: clean up test products (jdbc)

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

        given()
                .when().get("/products")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));

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
