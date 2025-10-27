package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.Model.Product;

import java.math.BigDecimal;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DataSeeder {

    @Inject
    EntityManager em;

    @Transactional
    public void loadDataOnStart(@Observes StartupEvent evt) {
        long productCount = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                .getSingleResult();

        if (productCount > 0) {
            System.out.println("DataSeeder: Product data already exists, skipping seed.");
            return;
        }

        System.out.println("DataSeeder: No products found. Seeding 100 new products...");

        for (int i = 1; i <= 100; i++) {
            String sku = "GR" + i;

            String name = "Sample Product " + i;

            BigDecimal price = new BigDecimal("10." + String.format("%02d", i-1));

            Product product = new Product(name, sku, price);

            em.persist(product);
        }

        System.out.println("DataSeeder: Successfully seeded 100 products.");
    }
}
