package it.swanswan.ProductService.service;

import it.swanswan.ProductService.dto.ProductRequest;
import it.swanswan.ProductService.dto.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    public void createProduct(ProductRequest productRequest);
    public List<ProductResponse> getProduct();
}
