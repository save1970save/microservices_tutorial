package it.swanswan.OrderService.service;

import it.swanswan.OrderService.dto.OrderRequest;
import it.swanswan.OrderService.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    public void placeOrder(OrderRequest orderRequest);
    public List<OrderResponse> getAllOrders();
}
