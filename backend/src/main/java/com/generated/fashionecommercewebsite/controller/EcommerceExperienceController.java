package com.generated.fashionecommercewebsite.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EcommerceExperienceController {
    private final List<Map<String, Object>> cartItems = new ArrayList<>();
    private final List<Map<String, Object>> wishlistItems = new ArrayList<>();
    private final List<Map<String, Object>> orders = new ArrayList<>();

    @GetMapping("/products/featured")
    public ResponseEntity<List<Map<String, Object>>> featuredProducts() {
        return ResponseEntity.ok(sampleProducts("featured"));
    }

    @GetMapping("/products/trending")
    public ResponseEntity<List<Map<String, Object>>> trendingProducts() {
        return ResponseEntity.ok(sampleProducts("trending"));
    }

    @GetMapping("/products/deals")
    public ResponseEntity<List<Map<String, Object>>> dealProducts() {
        return ResponseEntity.ok(sampleProducts("deals"));
    }

    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCart() {
        return ResponseEntity.ok(cartResponse());
    }

    @PostMapping("/cart/items")
    public ResponseEntity<Map<String, Object>> addCartItem(@RequestBody Map<String, Object> request) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("itemId", UUID.randomUUID().toString());
        item.put("productId", String.valueOf(request.getOrDefault("productId", UUID.randomUUID().toString())));
        item.put("variantId", String.valueOf(request.getOrDefault("variantId", UUID.randomUUID().toString())));
        item.put("brand", "StyleHub");
        item.put("name", "Neo Runner Jacket");
        item.put("imageUrl", "https://picsum.photos/seed/cart-item/600/800");
        item.put("size", "M");
        item.put("color", "Black");
        int quantity = intValue(request.get("quantity"), 1);
        BigDecimal unitPrice = BigDecimal.valueOf(1299);
        item.put("quantity", quantity);
        item.put("unitPrice", unitPrice);
        item.put("lineTotal", unitPrice.multiply(BigDecimal.valueOf(quantity)));
        cartItems.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse());
    }

    @PatchMapping("/cart/items/{itemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable String itemId, @RequestBody Map<String, Object> request) {
        int quantity = intValue(request.get("quantity"), 1);
        cartItems.removeIf(item -> itemId.equals(String.valueOf(item.get("itemId"))) && quantity <= 0);
        for (Map<String, Object> item : cartItems) {
            if (itemId.equals(String.valueOf(item.get("itemId")))) {
                BigDecimal unitPrice = decimalValue(item.get("unitPrice"), BigDecimal.ZERO);
                item.put("quantity", quantity);
                item.put("lineTotal", unitPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }
        return ResponseEntity.ok(cartResponse());
    }

    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(@PathVariable String itemId) {
        cartItems.removeIf(item -> itemId.equals(String.valueOf(item.get("itemId"))));
        return ResponseEntity.ok(cartResponse());
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Map<String, Object>> clearCart() {
        cartItems.clear();
        return ResponseEntity.ok(cartResponse());
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<Map<String, Object>>> getWishlist() {
        return ResponseEntity.ok(wishlistItems);
    }

    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<List<Map<String, Object>>> addWishlist(@PathVariable String productId) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("productId", productId);
        item.put("addedAt", Instant.now().toString());
        wishlistItems.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistItems);
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<List<Map<String, Object>>> removeWishlist(@PathVariable String productId) {
        wishlistItems.removeIf(item -> productId.equals(String.valueOf(item.get("productId"))));
        return ResponseEntity.ok(wishlistItems);
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("id", UUID.randomUUID().toString());
        order.put("status", "PLACED");
        order.put("createdAt", Instant.now().toString());
        order.put("items", new ArrayList<>(cartItems));
        order.put("total", cartResponse().get("grandTotal"));
        order.put("shippingAddress", request);
        orders.add(order);
        cartItems.clear();
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Map<String, Object>>> getOrders() {
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orders.stream()
            .filter(order -> id.equals(String.valueOf(order.get("id"))))
            .findFirst()
            .orElseGet(LinkedHashMap::new));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> adminStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("orders", orders.size());
        stats.put("cartItems", cartItems.size());
        stats.put("revenue", cartResponse().get("grandTotal"));
        stats.put("lowStock", 4);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<Map<String, Object>>> adminOrders() {
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin/inventory/low-stock")
    public ResponseEntity<List<Map<String, Object>>> lowStock() {
        return ResponseEntity.ok(sampleProducts("low-stock"));
    }

    @PatchMapping("/admin/inventory/{variantId}")
    public ResponseEntity<Map<String, Object>> updateInventory(@PathVariable String variantId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("variantId", variantId);
        response.put("stock", intValue(request.get("stock"), 10));
        response.put("updatedAt", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> cartResponse() {
        BigDecimal subtotal = cartItems.stream()
            .map(item -> decimalValue(item.get("lineTotal"), BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = subtotal.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        BigDecimal shipping = subtotal.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(49) : BigDecimal.ZERO;
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", cartItems);
        response.put("subtotal", subtotal);
        response.put("discount", discount);
        response.put("shipping", shipping);
        response.put("grandTotal", subtotal.subtract(discount).add(shipping));
        return response;
    }

    private List<Map<String, Object>> sampleProducts(String prefix) {
        List<Map<String, Object>> products = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Map<String, Object> product = new LinkedHashMap<>();
            product.put("id", UUID.randomUUID().toString());
            product.put("brand", i % 2 == 0 ? "StyleHub" : "NeoRunway");
            product.put("name", prefix + " fashion edit " + i);
            product.put("imageUrl", "https://picsum.photos/seed/" + prefix + i + "/600/800");
            product.put("secondaryImageUrl", "https://picsum.photos/seed/" + prefix + "b" + i + "/600/800");
            product.put("price", BigDecimal.valueOf(999 + i * 120L));
            product.put("mrp", BigDecimal.valueOf(1899 + i * 150L));
            product.put("rating", 4.0 + (i % 5) / 10.0);
            product.put("discountPercent", 30 + i);
            products.add(product);
        }
        return products;
    }

    private int intValue(Object value, int fallback) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private BigDecimal decimalValue(Object value, BigDecimal fallback) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
