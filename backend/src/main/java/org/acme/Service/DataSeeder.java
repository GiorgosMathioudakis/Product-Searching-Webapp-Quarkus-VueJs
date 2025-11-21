
package org.acme.Seeders;

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

    public void loadTenMillionData()
    {
        System.out.println("Starting bulk load...");
        long start = System.currentTimeMillis();

        try(StatelessSession session = sessionFactory.openStatelessSession()) {
            Transaction tx = session.beginTransaction();

            for (int i = 4378998; i < 10_000_000; i++) {
                Product p = new Product();
                p.name = "Product " + i;
                p.description = "Description for product " + i;
                p.sku = "SKU " + i;
                p.price = new BigDecimal(i / 1000);

                session.insert(p);

                if(i % 1000 == 0){
                    tx.commit();
                    tx = session.beginTransaction();
                    System.out.println("Inserted " + i + " products");
                }

            }

            tx.commit();

        }

        long end = System.currentTimeMillis();
        System.out.println("Finished in " + (end - start) / 1000 + " seconds.");

    }

}