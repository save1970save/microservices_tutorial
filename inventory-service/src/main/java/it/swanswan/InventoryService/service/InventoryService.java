package it.swanswan.InventoryService.service;

import it.swanswan.InventoryService.dto.InventoryResponse;
import it.swanswan.InventoryService.dto.ProductInStockResponse;

import java.util.List;

public interface InventoryService {
    public boolean isInStock(String productCode);
    public InventoryResponse getQuantityInStock(String productCode);
    public List<ProductInStockResponse> productsIsInStock (List<String> productCodes);
}
