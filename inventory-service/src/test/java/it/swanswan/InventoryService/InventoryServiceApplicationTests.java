package it.swanswan.InventoryService;

import com.jayway.jsonpath.JsonPath;
import it.swanswan.InventoryService.model.Inventory;
import it.swanswan.InventoryService.repository.InventoryRepo;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// NON SO PERCHE MA SE NON CE L'APPLICATION.PROPERTIES FALLISCE
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InventoryRepo inventoryRepo;

	@Container
	public static PostgreSQLContainer postgreSQLContainer  = new PostgreSQLContainer("postgres:13.4-buster")
			.withDatabaseName("databasename")
			.withUsername("sa")
			.withPassword("sa");;

	@DynamicPropertySource
	static void registerPgProperties(@NotNull DynamicPropertyRegistry registry) {
		// QUESTO E' FONDAMENTALE SE NO SI SPACCA
		registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:13.4-buster:///databasename");
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Test
	@DisplayName("getQuantityInStockTest")
	public void getQuantityInStockTest() throws Exception {
		this.initializeDB();

		mockMvc.perform(get("/api/inventory/Atala")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity").value(23));

		mockMvc.perform(get("/api/inventory/Colnago")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity").value(0));

		mockMvc.perform(get("/api/inventory/Unknown")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Unknown Product Code Unknown")));

	}


	@Test
	@DisplayName("isInStockTest")
	public void isInStockTest() throws Exception {
		this.initializeDB();

		mockMvc.perform(get("/api/inventory/instock/Atala")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		mockMvc.perform(get("/api/inventory/instock/Colnago")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("false"));

		mockMvc.perform(get("/api/inventory/instock/Unknown")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Unknown Product Code Unknown")));

	}

	private void initializeDB () {
		inventoryRepo.deleteAll();
		inventoryRepo.save(Inventory.builder()
				.productCode("Atala")
				.quantity(23)
				.build());
		inventoryRepo.save(Inventory.builder()
				.productCode("Colnago")
				.quantity(0)
				.build());
		inventoryRepo.save(Inventory.builder()
				.productCode("De Rosa")
				.quantity(8)
				.build());
	}
}
