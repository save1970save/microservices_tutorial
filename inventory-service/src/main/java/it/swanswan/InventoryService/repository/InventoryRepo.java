package it.swanswan.InventoryService.repository;


import it.swanswan.InventoryService.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductCode(String productCode);
    List<Inventory> findByProductCodeIn(List<String> productCodes);
}
