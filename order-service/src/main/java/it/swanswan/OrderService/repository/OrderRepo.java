package it.swanswan.OrderService.repository;

import it.swanswan.OrderService.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
