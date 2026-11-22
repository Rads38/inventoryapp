package com.retail.inventory.repository;

import com.retail.inventory.domain.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItemIgnoreCase(String item);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select inventory
            from Inventory inventory
            where lower(inventory.item) = lower(:item)
            """)
    Optional<Inventory> findByItemForUpdate(@Param("item") String item);
}
