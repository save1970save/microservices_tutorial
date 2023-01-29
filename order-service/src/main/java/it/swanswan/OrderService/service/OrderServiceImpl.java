package it.swanswan.OrderService.service;

import it.swanswan.OrderService.dto.*;
import it.swanswan.OrderService.exceptions.CustomErrorException;
import it.swanswan.OrderService.model.Order;
import it.swanswan.OrderService.model.OrderLine;
import it.swanswan.OrderService.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLine> orderLineList = orderRequest.getOrderLinesRequest()
                .stream().map(this::mapFromDto).toList();

        WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();

        List<ProductInStockResponse> response = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/inventory/instock")
                        .queryParam("pcode", String.join(",", orderLineList.stream().map(OrderLine::getProductCode).toList()))
                        .build())
                .retrieve()
                .bodyToFlux(ProductInStockResponse.class)
                .collectList()
                .block();

        if (response.stream().allMatch(el -> el.getInStock().equals(true))) {
            order.setOrderLines(orderLineList);
            orderRepo.save(order);
        } else {
            throw new CustomErrorException("Products not in Stock to place the order !");
        }
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        return orders.stream().map(this::mapToDto).toList();
    }

    private OrderLine mapFromDto(OrderLineRequest orderLineItemsDto) {
        return OrderLine.builder()
                .productCode(orderLineItemsDto.getProductCode())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }

    private OrderResponse mapToDto(Order order) {
        List<OrderLineResponse> orderLinesResponse =
                order.getOrderLines()
                        .stream().map(orderLine -> {
                            return OrderLineResponse.builder()
                                    .id(orderLine.getId())
                                    .productCode(orderLine.getProductCode())
                                    .price(orderLine.getPrice())
                                    .quantity(orderLine.getQuantity())
                                    .build();
                        }).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLinesResponse(orderLinesResponse)
                .build();

    }
}
