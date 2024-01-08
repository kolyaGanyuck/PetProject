package customloginapplication.repositories;

import customloginapplication.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
 List<Order> findOrderByUserId(Long id);
 Order findOrderByProductId(Long id);
}
