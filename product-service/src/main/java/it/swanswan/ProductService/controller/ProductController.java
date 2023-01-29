package it.swanswan.ProductService.controller;

import it.swanswan.ProductService.dto.ProductRequest;
import it.swanswan.ProductService.dto.ProductResponse;
import it.swanswan.ProductService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createProduct (@RequestBody ProductRequest productRequest) {
            productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ProductResponse> getProduct () {
        return productService.getProduct();
    }
}
