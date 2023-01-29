package it.swanswan.InventoryService;

import it.swanswan.InventoryService.model.Inventory;
import it.swanswan.InventoryService.repository.InventoryRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class  InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initializeDB (InventoryRepo inventoryRepo) {
		return args -> {
			inventoryRepo.deleteAll();
				inventoryRepo.save(Inventory.builder()
					.productCode("Bottecchia")
					.quantity(23)
					.build());
			inventoryRepo.save(Inventory.builder()
					.productCode("Vicini")
					.quantity(0)
					.build());
			inventoryRepo.save(Inventory.builder()
					.productCode("Faini")
					.quantity(8)
					.build());
		};
	}

}
