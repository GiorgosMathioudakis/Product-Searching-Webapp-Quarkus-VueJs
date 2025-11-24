package org.acme.Service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.Model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

import java.math.BigDecimal;


@ApplicationScoped
public class DataSeeder {

    @Inject
    SessionFactory sessionFactory;

    // 10 Real-sounding product names
    private static final String[] BASE_NAMES = {
            "Wireless Ergonomic Mouse",
            "Mechanical Gaming Keyboard",
            "Ultra-Wide 4K Monitor",
            "Noise Cancelling Headphones",
            "Smart Home Assistant",
            "Portable SSD Drive",
            "USB-C Multiport Adapter",
            "High-Performance Laptop",
            "Smartphone Gimbal Stabilizer",
            "Bluetooth Mesh Router"
    };

    // 10 Distinct 2-letter SKU prefixes corresponding roughly to the items above
    private static final String[] SKU_PREFIXES = {
            "WM", // Wireless Mouse
            "GK", // Gaming Keyboard
            "QM", // Quad/Quality Monitor
            "NH", // Noise Headphones
            "SA", // Smart Assistant
            "PD", // Portable Drive
            "UA", // USB Adapter
            "HL", // High Laptop
            "SG", // Smartphone Gimbal
            "BR"  // Bluetooth Router
    };

    public void loadTenMillionData() {
        System.out.println("Starting bulk load...");
        long start = System.currentTimeMillis();

        try (StatelessSession session = sessionFactory.openStatelessSession()) {
            Transaction tx = session.beginTransaction();

            // Resuming from your specific index
            for (int i = 1; i <= 10_000_000; i++) {

                // This cycles 0 through 9 repeatedly
                int index = i % 10;

                Product p = new Product();

                // Example: "Wireless Ergonomic Mouse 4387871"
                p.name = BASE_NAMES[index] + " " + i;

                // Example: "Description for Wireless Ergonomic Mouse 4387871"
                p.description = "Description for " + BASE_NAMES[index] + " " + i;

                // Example: "WM4387871" (Unique because of i)
                p.sku = SKU_PREFIXES[index] + i;

                // Generates a price between 10.00 and 110.00 (more realistic than i/1000)
                // Or keep your logic: p.price = new BigDecimal(i / 1000);
                p.price = new BigDecimal((i % 100) + 10);

                session.insert(p);

                if (i % 10000 == 0) {
                    tx.commit();
                    tx = session.beginTransaction();
                    System.out.println("Inserted " + i + " products...");
                }
            }

            tx.commit();
        }

        long end = System.currentTimeMillis();
        System.out.println("Finished in " + (end - start) / 1000 + " seconds.");
    }
}