package it.swanswan.InventoryService.service;

import it.swanswan.InventoryService.dto.InventoryResponse;
import it.swanswan.InventoryService.dto.ProductInStockResponse;
import it.swanswan.InventoryService.exceptions.CustomErrorException;
import it.swanswan.InventoryService.model.Inventory;
import it.swanswan.InventoryService.repository.InventoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;

    public List<ProductInStockResponse> productsIsInStock(List<String> productCodes) {
        List<Inventory> inventories = inventoryRepo.findByProductCodeIn(productCodes);

        if (inventories.size() > 0) {
            return inventories.stream().map(el -> {
                return ProductInStockResponse.builder()
                        .productCode(el.getProductCode())
                        .inStock(el.getQuantity() > 0 ? true : false)
                        .build();
            }).toList();
        } else {
            throw  new CustomErrorException(String.format("Unknown products %s", productCodes));
        }
    }

    public boolean isInStock(String productCode) {
        List<Inventory> inventories = inventoryRepo.findByProductCode(productCode);
        if (inventories.size() > 0) {
            return inventories.get(0).getQuantity() > 0 ? true : false;
        } else {
            throw  new CustomErrorException(String.format("Unknown Product Code %s", productCode));
        }
    }

    public InventoryResponse getQuantityInStock(String productCode) throws CustomErrorException {
        List<Inventory> inventories = inventoryRepo.findByProductCode(productCode);
        if (inventories.size() > 0) {
            return InventoryResponse.builder()
                    .id(inventories.get(0).getId())
                    .quantity(inventories.get(0).getQuantity())
                    .productCode(inventories.get(0).getProductCode())
                    .build();
        } else {
            throw  new CustomErrorException(String.format("Unknown Product Code %s", productCode));
        }

    }
}
