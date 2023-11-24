package customloginapplication.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long orderId;
    private Long userId;
    private Long productId;
    private LocalDateTime dataOfCreated;
    private boolean isReserved;
    private int count;

    public Order(Long productId, Long userId, LocalDateTime now, int price) {
        this.productId = productId;
        this.userId = userId;
        isReserved = false;
        count = 1;
    }


    @PrePersist
    private void init() {
        dataOfCreated = LocalDateTime.now();
    }
}
