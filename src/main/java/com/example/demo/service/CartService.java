package com.example.demo.service;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }


    @Transactional
    public CartItemResponse addItem(Integer userId, CartItemRequest request) {

        if (userId == null || request == null ||
                request.productId() == null ||
                request.quantity() == null) {

            throw new IllegalArgumentException(
                    "userId, productId, and quantity are required"
            );
        }

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );

        // ✅ STOCK VALIDATION
        if (request.quantity() > product.getStock()) {
            throw new RuntimeException(
                    "Only " + product.getStock() + " items available in stock"
            );
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUserId(userId);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());

                    return cartRepository.save(newCart);
                });

        LocalDateTime now = LocalDateTime.now();

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        request.productId()
                )
                .orElseGet(CartItem::new);

        if (cartItem.getId() == null) {

            cartItem.setCart(cart);
            cartItem.setProductId(request.productId());
            cartItem.setQuantity(request.quantity());

            cartItem.setAddedPrice(
                    request.addedPrice() == null
                            ? BigDecimal.ZERO
                            : request.addedPrice()
            );

            cartItem.setCreatedAt(now);

        } else {

            Integer newQuantity =
                    cartItem.getQuantity() + request.quantity();

            // ✅ VALIDATE UPDATED QUANTITY
            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Only " + product.getStock() + " items available in stock"
                );
            }

            cartItem.setQuantity(newQuantity);

            if (request.addedPrice() != null) {
                cartItem.setAddedPrice(request.addedPrice());
            }
        }

        cart.setUpdatedAt(now);
        cartItem.setUpdatedAt(now);

        CartItem savedItem = cartItemRepository.save(cartItem);

        return new CartItemResponse(
                savedItem.getId(),
                savedItem.getProductId(),
                product.getName(),
                product.getImage(),
                savedItem.getQuantity(),
                product.getStock(),
                savedItem.getAddedPrice(),
                savedItem.getCreatedAt(),
                savedItem.getUpdatedAt()
        );
    }

    // =========================================================
    // GET CART
    // =========================================================

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        return buildCartResponse(userId);
    }

    // =========================================================
    // UPDATE ITEM
    // =========================================================

    @Transactional
    public CartItemResponse updateItem(
            Integer itemId,
            CartItemRequest request
    ) {

        if (itemId == null ||
                request == null ||
                request.quantity() == null) {

            throw new IllegalArgumentException(
                    "itemId and quantity are required"
            );
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found")
                );

        Product product = productRepository
                .findById(item.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );

        // ✅ STOCK VALIDATION
        if (request.quantity() > product.getStock()) {
            throw new RuntimeException(
                    "Only " + product.getStock() + " items available in stock"
            );
        }

        if (request.quantity() <= 0) {

            item.getCart().getItems()
                    .removeIf(existing ->
                            existing.getId().equals(itemId));

            item.getCart().setUpdatedAt(LocalDateTime.now());

            return new CartItemResponse(
                    itemId,
                    item.getProductId(),
                    product.getName(),
                    product.getImage(),
                    0,
                    product.getStock(),
                    item.getAddedPrice(),
                    item.getCreatedAt(),
                    LocalDateTime.now()
            );
        }

        item.setQuantity(request.quantity());

        if (request.addedPrice() != null) {
            item.setAddedPrice(request.addedPrice());
        }

        item.setUpdatedAt(LocalDateTime.now());
        item.getCart().setUpdatedAt(LocalDateTime.now());

        cartRepository.save(item.getCart());

        CartItem saved = cartItemRepository.save(item);

        return new CartItemResponse(
                saved.getId(),
                saved.getProductId(),
                product.getName(),
                product.getImage(),
                saved.getQuantity(),
                product.getStock(),
                saved.getAddedPrice(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    // =========================================================
    // REMOVE ITEM
    // =========================================================

    @Transactional
    public void removeItem(Integer itemId, Integer userId) {

        if (itemId == null) {
            throw new IllegalArgumentException("itemId is required");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found")
                );

        if (userId != null &&
                !userId.equals(item.getCart().getUserId())) {

            throw new IllegalArgumentException(
                    "Cart item does not belong to the specified user"
            );
        }

        Cart cart = item.getCart();

        cart.getItems().removeIf(existing ->
                existing.getId().equals(itemId));

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    @Transactional
    public void clearCart(Integer userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);

        if (cart == null) {
            return;
        }

        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private CartResponse buildCartResponse(Integer userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);

        if (cart == null) {

            return new CartResponse(
                    null,
                    List.of(),
                    null,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        List<CartItemResponse> items = new ArrayList<>();

        BigDecimal subtotal = calculateSubtotal(cart);

        for (CartItem item : cart.getItems()) {

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElse(null);

            items.add(new CartItemResponse(
                    item.getId(),
                    item.getProductId(),
                    product != null ? product.getName() : null,
                    product != null ? product.getImage() : null,
                    item.getQuantity(),
                    product != null ? product.getStock() : 0,
                    item.getAddedPrice(),
                    item.getCreatedAt(),
                    item.getUpdatedAt()
            ));
        }

        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal total = subtotal;

        return new CartResponse(
                cart.getId(),
                items,
                null,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                discount.setScale(2, RoundingMode.HALF_UP),
                total.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateSubtotal(Cart cart) {

        return cart.getItems().stream()
                .map(i ->
                        i.getAddedPrice().multiply(
                                BigDecimal.valueOf(i.getQuantity())
                        )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}