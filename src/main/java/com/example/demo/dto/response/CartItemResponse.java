package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private Integer productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;

    // Snake_case aliases so the uiparts Cart.tsx (which reads product_id,
    // line_total) sees the same values without any extra mapping work.
    @JsonProperty("product_id")
    public Integer getProductIdSnake() {
        return productId;
    }

    @JsonProperty("line_total")
    public BigDecimal getLineTotalSnake() {
        return lineTotal;
    }
}
