package it.swanswan.InventoryService.controller;

import it.swanswan.InventoryService.dto.InventoryResponse;
import it.swanswan.InventoryService.dto.ProductInStockResponse;
import it.swanswan.InventoryService.exceptions.CustomErrorException;
import it.swanswan.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private  InventoryService inventoryService;

    @GetMapping("/instock/{product_code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("product_code") String productCode) {
        try {
            return inventoryService.isInStock(productCode);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/instock")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductInStockResponse> productsIsInStock(@RequestParam("pcode") List<String> productCodes) {
        try {
            return inventoryService.productsIsInStock(productCodes);
        } catch (CustomErrorException e2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage(), e2);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{product_code}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse getQuantityInStock(@PathVariable("product_code") String productCode) {
        try {
            return inventoryService.getQuantityInStock(productCode);
        } catch (CustomErrorException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exc.getMessage(), exc);
        }
    }
}
