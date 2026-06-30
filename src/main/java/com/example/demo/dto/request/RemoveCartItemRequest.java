package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveCartItemRequest {

    @JsonAlias({"product_id"})
    @NotNull(message = "productId is required")
    private Integer productId;
}
