package customloginapplication.services;

import customloginapplication.models.Order;
import customloginapplication.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@org.springframework.stereotype.Service
@Slf4j

public class OrderService  {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order findById(Long id){
        return orderRepository.findById(id).orElseThrow(null);
    }
    public void save (Order order){
        orderRepository.save(order);
    }

    public List<Order> findOrderByUserId(Long id){
        return orderRepository.findOrderByUserId(id);
    }
}
