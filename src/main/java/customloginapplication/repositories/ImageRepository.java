package customloginapplication.repositories;

import customloginapplication.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository <Image,Long>{
}
