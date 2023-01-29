package it.swanswan.OrderService.controller;

import it.swanswan.OrderService.dto.OrderRequest;
import it.swanswan.OrderService.dto.OrderResponse;
import it.swanswan.OrderService.exceptions.CustomErrorException;
import it.swanswan.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.placeOrder(orderRequest);
            return new ResponseEntity<>("Order placed successfully",HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof CustomErrorException) {
               // throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
                return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}
