package org.acme;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.acme.Service.DataSeeder;

@QuarkusMain(name = "db-seeder")
public class DataSeederMain implements QuarkusApplication {

    @Inject
    DataSeeder dataSeeder;

    @Override
    public int run(String... args) throws Exception {

        System.out.println("Starting DataSeederMain");

        dataSeeder.loadTenMillionData();

        return 0;
    }


}
