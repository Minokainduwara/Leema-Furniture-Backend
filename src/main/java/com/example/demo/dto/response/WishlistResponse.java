package com.example.demo.dto.response;

import com.example.demo.dto.response.WishlistItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {

    private Integer id;

    private String userEmail;

    private List<WishlistItemResponse> items;
}