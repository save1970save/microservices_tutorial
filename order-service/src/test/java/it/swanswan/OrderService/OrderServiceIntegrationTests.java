package it.swanswan.OrderService;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ParseException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import it.swanswan.OrderService.dto.OrderLineRequest;
import it.swanswan.OrderService.dto.OrderRequest;
import it.swanswan.OrderService.dto.ProductInStockResponse;
import it.swanswan.OrderService.model.Order;
import it.swanswan.OrderService.model.OrderLine;
import it.swanswan.OrderService.repository.OrderRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceIntegrationTests {

	private final String mysql_container = "";

	@Container
	private static MySQLContainer container = new MySQLContainer("mysql:latest")
			.withDatabaseName("example_db");

	@DynamicPropertySource
	static void registerPgProperties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.username", container::getUsername);
		registry.add("spring.datasource.password", container::getPassword);
		registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
	}

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebClient webClient;

	/* non servono
	@BeforeAll
	public static void  setUp() {
		container.withReuse(true);
		container.withInitScript("bla bla bla");
		container.start();
	}

	@AfterAll
	public static void  stop() {
		container.stop();
	}
	*/

	@BeforeEach
	void cleanup () {
		orderRepo.deleteAll();
	}

	@Test
	void  checkProductsInStockTest() throws IOException, ParseException {
		List<ProductInStockResponse> response = webClient.get().uri(uriBuilder -> uriBuilder
						.path("/api/inventory/instock")
						.queryParam("pcode", "Bottecchia,Vicini" )
						//.queryParam("pcode", "Vicini" )
						.build())
				.retrieve()
				.bodyToFlux(ProductInStockResponse.class)
				.collectList()
				.block();

		System.out.println("OK test passed");
	}

	void printError(Exception e) {
		System.out.println(e.getStackTrace().toString());
	}

	@Test
	void placeOrderTest() throws Exception {

		OrderRequest orderRequest = OrderRequest.builder()
				.orderLinesRequest(List.of(
						OrderLineRequest.builder()
								.productCode("Santa Cruz")
								.price(BigDecimal.valueOf(156.98))
								.quantity(13)
								.build(),
						OrderLineRequest.builder()
								.productCode("Merida")
								.price(BigDecimal.valueOf(116.28))
								.quantity(1)
								.build(),
						OrderLineRequest.builder()
								.productCode("Trak")
								.price(BigDecimal.valueOf(96.21))
								.quantity(5)
								.build()
						))
				.build();

		mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjToString(orderRequest)))
				.andExpect(status().isCreated());

		Assertions.assertEquals(orderRepo.findAll().size(), 1);
	}

	@Test
	void getAllOrdersTest() throws Exception {

		Order order = Order.builder()
				.orderNumber(UUID.randomUUID().toString())
				.orderLines(List.of(
						OrderLine.builder()
								.productCode("Santa Cruz")
								.price(BigDecimal.valueOf(156.98))
								.quantity(13)
								.build(),
						OrderLine.builder()
								.productCode("Merida")
								.price(BigDecimal.valueOf(116.28))
								.quantity(1)
								.build(),
						OrderLine.builder()
								.productCode("Trak")
								.price(BigDecimal.valueOf(96.21))
								.quantity(5)
								.build()
				))
		.build();

		orderRepo.save(order);

		mockMvc.perform(get("/api/order")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*", hasSize(1) ))
				.andExpect(jsonPath("$.[0].orderNumber").isNotEmpty())
				.andExpect(jsonPath("$.[0].orderLinesResponse", hasSize(3)))
				.andExpect(jsonPath("$.[0].orderLinesResponse[0].productCode").value("Santa Cruz"))
				.andExpect(jsonPath("$.[0].orderLinesResponse[0].price").value(BigDecimal.valueOf(156.98)))
				.andExpect(jsonPath("$.[0].orderLinesResponse[0].quantity").value(13));
		/*
				.andExpect(jsonPath("$.orderLinesResponse[0].productCode[0]").value("Santa Cruz"))
				.andExpect(jsonPath("$.orderLinesResponse[0].price[0]").value(BigDecimal.valueOf(156.98)))
				.andExpect(jsonPath("$.orderLinesResponse[0].quantity[0]").value(13));
		*/
	}

	private  String convertObjToString(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}


}
