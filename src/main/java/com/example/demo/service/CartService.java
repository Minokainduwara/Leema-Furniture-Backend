package com.example.demo.service;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.Product;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final CouponProductRepository couponProductRepository;
    private final CouponCategoryRepository couponCategoryRepository;
    private final ProductRepository productRepository;

    private final Map<Integer, String> appliedCouponByUser = new ConcurrentHashMap<>();

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CouponRepository couponRepository,
            CouponProductRepository couponProductRepository,
            CouponCategoryRepository couponCategoryRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.couponRepository = couponRepository;
        this.couponProductRepository = couponProductRepository;
        this.couponCategoryRepository = couponCategoryRepository;
        this.productRepository = productRepository;
    }

    // ADD ITEM 

    @Transactional
    public CartItemResponse addItem(Integer userId, CartItemRequest request) {

        if (userId == null || request == null || request.productId() == null || request.quantity() == null) {
            throw new IllegalArgumentException("userId, productId, and quantity are required");
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

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId())
                .orElseGet(CartItem::new);

        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProductId(request.productId());
            cartItem.setQuantity(request.quantity());
            cartItem.setAddedPrice(request.addedPrice() == null ? BigDecimal.ZERO : request.addedPrice());
            cartItem.setCreatedAt(now);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
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
                productRepository.findById(savedItem.getProductId()).map(Product::getName).orElse(null),
                savedItem.getQuantity(),
                savedItem.getAddedPrice(),
                savedItem.getCreatedAt(),
                savedItem.getUpdatedAt()
        );
    }

    // GET CART 

    @Transactional(readOnly = true)
    public CartResponse getCart(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        return buildCartResponse(userId);
    }

    // UPDATE ITEM 

    @Transactional
    public CartItemResponse updateItem(Integer itemId, CartItemRequest request) {

        if (itemId == null || request == null || request.quantity() == null) {
            throw new IllegalArgumentException("itemId and quantity are required");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (request.quantity() <= 0) {
            item.getCart().getItems().removeIf(existing -> existing.getId().equals(itemId));
            item.getCart().setUpdatedAt(LocalDateTime.now());

            return new CartItemResponse(
                    itemId,
                    item.getProductId(),
                    productRepository.findById(item.getProductId()).map(Product::getName).orElse(null),
                    0,
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
                productRepository.findById(saved.getProductId()).map(Product::getName).orElse(null),
                saved.getQuantity(),
                saved.getAddedPrice(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    // REMOVE ITEM

    @Transactional
    public void removeItem(Integer itemId, Integer userId) {

        if (itemId == null) {
            throw new IllegalArgumentException("itemId is required");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (userId != null && !userId.equals(item.getCart().getUserId())) {
            throw new IllegalArgumentException("Cart item does not belong to the specified user");
        }

        Cart cart = item.getCart();
        cart.getItems().removeIf(existing -> existing.getId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
    }

    // CLEAR CART 

    @Transactional
    public void clearCart(Integer userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            return;
        }

        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
        appliedCouponByUser.remove(userId);
    }

    // APPLY COUPON 

    @Transactional
    public CartResponse applyCoupon(Integer userId, CouponRequest request) {

        if (userId == null || request == null || request.couponCode() == null || request.couponCode().isBlank()) {
            throw new IllegalArgumentException("userId and couponCode are required");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        BigDecimal subtotal = calculateSubtotal(cart);

        Coupon coupon = couponRepository.findByCode(request.couponCode())
                .orElseThrow(() -> new IllegalArgumentException("Coupon is invalid or inactive"));

        if (!Boolean.TRUE.equals(coupon.getIsActive()) || !isCouponApplicable(coupon, cart, subtotal)) {
            throw new IllegalArgumentException("Coupon is invalid or not applicable");
        }

        coupon.setCurrentUses((coupon.getCurrentUses() == null ? 0 : coupon.getCurrentUses()) + 1);
        couponRepository.save(coupon);

        appliedCouponByUser.put(userId, request.couponCode());

        return buildCartResponse(userId);
    }

    // REMOVE COUPON 

    @Transactional
    public CartResponse removeCoupon(Integer userId) {
        appliedCouponByUser.remove(userId);
        return buildCartResponse(userId);
    }

    // HELPER METHODS

    private CartResponse buildCartResponse(Integer userId) {

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            return new CartResponse(null, List.of(), null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal subtotal = calculateSubtotal(cart);

        for (CartItem item : cart.getItems()) {
            items.add(new CartItemResponse(
                    item.getId(),
                    item.getProductId(),
                    productRepository.findById(item.getProductId()).map(Product::getName).orElse(null),
                    item.getQuantity(),
                    item.getAddedPrice(),
                    item.getCreatedAt(),
                    item.getUpdatedAt()
            ));
        }

        String couponCode = appliedCouponByUser.get(userId);
        BigDecimal discount = calculateDiscount(cart, couponCode, subtotal);

        BigDecimal total = subtotal.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        return new CartResponse(
                cart.getId(),
                items,
                couponCode,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                discount.setScale(2, RoundingMode.HALF_UP),
                total.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(i -> i.getAddedPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(Cart cart, String couponCode, BigDecimal subtotal) {

        if (couponCode == null) return BigDecimal.ZERO;

        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(couponCode).orElse(null);
        if (coupon == null) return BigDecimal.ZERO;

        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            return subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
        }

        return coupon.getDiscountValue();
    }

    private boolean isCouponApplicable(Coupon coupon, Cart cart, BigDecimal subtotal) {

        LocalDate today = LocalDate.now();

        return (coupon.getValidFrom() == null || !today.isBefore(coupon.getValidFrom()))
                && (coupon.getValidUntil() == null || !today.isAfter(coupon.getValidUntil()))
                && (coupon.getMinOrderAmount() == null || subtotal.compareTo(coupon.getMinOrderAmount()) >= 0);
    }
}