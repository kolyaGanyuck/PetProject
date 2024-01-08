package customloginapplication.services;

import customloginapplication.models.Order;
import customloginapplication.models.Product;
import customloginapplication.repositories.OrderRepository;
import customloginapplication.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Slf4j

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(null);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findOrderByUserId(Long id) {
        return orderRepository.findOrderByUserId(id);
    }

    public void reserveOrder(Long id) {
        Order order = orderRepository.findOrderByProductId(id);
        if (order != null) {
            order.setReserved(true);
            orderRepository.save(order);
        }
    }
}
