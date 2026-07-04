package com.example.demo.service;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.RemoveCartItemRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return buildResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest req) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .addedPrice(product.getPrice() == null ? BigDecimal.ZERO : product.getPrice())
                        .build());

        item.setQuantity(item.getQuantity() + req.getQuantity());
        cartItemRepository.save(item);

        return buildResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String email, UpdateCartItemRequest req) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not in cart"));

        if (req.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        return buildResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String email, RemoveCartItemRequest req) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresent(cartItemRepository::delete);

        return buildResponse(cart);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = cartRepository.findByUser(getUser(email)).orElse(null);
        if (cart == null) return;
        cartItemRepository.deleteByCart(cart);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Cart getOrCreateCart(String email) {
        User user = getUser(email);
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private CartResponse buildResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart(cart);

        List<CartItemResponse> dtos = items.stream().map(it -> {
            BigDecimal price = it.getAddedPrice() == null ? BigDecimal.ZERO : it.getAddedPrice();
            BigDecimal line = price.multiply(BigDecimal.valueOf(it.getQuantity()));
            return CartItemResponse.builder()
                    .productId(it.getProduct().getId())
                    .productName(it.getProduct().getName())
                    .productImage(it.getProduct().getImage())
                    .quantity(it.getQuantity())
                    .price(price)
                    .lineTotal(line)
                    .build();
        }).toList();

        BigDecimal total = dtos.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder().items(dtos).total(total).build();
    }
}
