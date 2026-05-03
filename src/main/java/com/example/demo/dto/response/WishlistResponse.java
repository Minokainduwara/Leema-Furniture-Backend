package com.example.demo.dto.response;

import java.util.List;

public record WishlistResponse(
        Integer wishlistId,
        List<WishlistItemResponse> items
) {
}