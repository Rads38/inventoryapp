package com.retail.inventory.config;

import com.retail.inventory.domain.Inventory;
import com.retail.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryDataInitializer {

    @Bean
    CommandLineRunner initializeInventory(InventoryRepository repository) {
        return args -> {
            addIfMissing(repository, "Laptop", 10);
            addIfMissing(repository, "Mobile", 20);
            addIfMissing(repository, "Headphones", 15);
        };
    }

    private void addIfMissing(
            InventoryRepository repository,
            String item,
            int quantity
    ) {
        repository.findByItemIgnoreCase(item)
                .orElseGet(() -> repository.save(new Inventory(item, quantity)));
    }
}
