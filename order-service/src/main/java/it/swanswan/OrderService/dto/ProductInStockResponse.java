package it.swanswan.OrderService.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductInStockResponse {
    private String productCode;
    private Boolean inStock;
}
