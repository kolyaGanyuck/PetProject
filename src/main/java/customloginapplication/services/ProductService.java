package customloginapplication.services;

import customloginapplication.models.Image;
import customloginapplication.models.Product;
import customloginapplication.repositories.ImageRepository;
import customloginapplication.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Service
@Slf4j

public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }



    public List<Product> getProducts(){
        return productRepository.findAll();
    }

    public Product findById(Long id){
       return productRepository.findById(id).orElse(null);
    }

    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findByIdIn(productIds);
    }



    public void saveProduct(Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        Image image1;
        Image image2;
        Image image3;
        if (file1.getSize() != 0){
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);

        }
        if (file2.getSize() != 0){
            image2 = toImageEntity(file2);
            product.addImageToProduct(image2);

        }
        if (file3.getSize() != 0){
            image3 = toImageEntity(file3);
            product.addImageToProduct(image3);

        }
        log.info("Saving new Product. Title: {}; Author: {}", product.getTitle(), product.getAuthor());

        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        productRepository.save(product);
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
    Image image = new Image();
    image.setName(file.getName());
    image.setOriginalFileName(file.getOriginalFilename());
    image.setContentType(file.getContentType());
    image.setSize(file.getSize());
    image.setBytes(file.getBytes());
    return image;

    }
}

