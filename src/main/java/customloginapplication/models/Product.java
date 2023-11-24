package customloginapplication.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "power")
    private int power;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "author")
    private String author;
    @Column(name = "maxSpeed")
    private int maxSpeed;
    @Column(name = "price")
    private int price;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<Image> images = new ArrayList<>();
    private Long previewImageId;
    private LocalDateTime dataOfCreated;



    @PrePersist
    private void init(){
        dataOfCreated = LocalDateTime.now();
    }
    public void addImageToProduct(Image image){
        image.setProduct(this);
        images.add(image);
    }
}
