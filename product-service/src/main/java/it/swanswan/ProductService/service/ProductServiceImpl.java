package it.swanswan.ProductService.service;

import it.swanswan.ProductService.dto.ProductRequest;
import it.swanswan.ProductService.dto.ProductResponse;
import it.swanswan.ProductService.model.Product;
import it.swanswan.ProductService.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public void createProduct(ProductRequest productRequest) {
            Product product = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .description(productRequest.getDescription())
                    .name(productRequest.getName())
                    .price(productRequest.getPrice())
                    .build();
            productRepo.save(product);
            log.info("Product {} created ", productRequest.getName());
    }

    @Override
    public List<ProductResponse> getProduct() {
        List<Product> products = productRepo.findAll();
        return products.stream().map(this::mapToDto).toList();
    }

    private ProductResponse mapToDto (Product product) {
      return ProductResponse.builder()
              .id(product.getId())
              .name(product.getName())
              .description(product.getDescription())
              .price(product.getPrice())
              .build();
    }
}
