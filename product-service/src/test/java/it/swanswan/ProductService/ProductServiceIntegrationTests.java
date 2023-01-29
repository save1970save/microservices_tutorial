package it.swanswan.ProductService;

import it.swanswan.ProductService.dto.ProductRequest;
import it.swanswan.ProductService.model.Product;
import it.swanswan.ProductService.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceIntegrationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.6");

	@DynamicPropertySource
	static void registerPgProperties(DynamicPropertyRegistry registry){
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void cleanup () {
		productRepo.deleteAll();
	}

	@Test
	void createProductTest() throws Exception {

		ProductRequest productRequest = ProductRequest.builder()
				.name("Connonaide")
				.description("Bike Connonaide")
				.price(BigDecimal.valueOf(123.89))
				.build();

		mockMvc.perform(post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjToString(productRequest)))
				.andDo(print())
				.andExpect(status().isCreated());

		Assertions.assertEquals(productRepo.findAll().size(), 1);

	}

	@Test
	void   getAllProductsTest() throws Exception {

		Product product = Product.builder()
				.name("Connonaide")
				.description("Bike Connonaide")
				.price(BigDecimal.valueOf(123.89))
				.build();

		productRepo.save(product);

		mockMvc.perform(get("/api/product")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$[0].name").value("Connonaide"))
				.andExpect(jsonPath("$[0].description").value("Bike Connonaide"))
				.andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(123.89)))
				.andExpect(jsonPath("$.*", hasSize(1)));

	}

	private static String convertObjToString(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
	}
}
