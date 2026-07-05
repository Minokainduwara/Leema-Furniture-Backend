package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getAnalytics() {
        return getAnalytics(null, null, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAnalytics(String period, String from, String to) {
        List<Order> allOrders = orderRepository.findAllWithItemsAndProducts();
        List<Product> allProducts = productRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yearStart = now.withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime filterStart = null;
        LocalDateTime filterEnd = null;

        if (from != null && !from.isEmpty()) {
            filterStart = LocalDate.parse(from).atStartOfDay();
        }
        if (to != null && !to.isEmpty()) {
            filterEnd = LocalDate.parse(to).plusDays(1).atStartOfDay();
        }
        if (period != null) {
            switch (period) {
                case "30days" -> filterStart = now.minusDays(30);
                case "90days" -> filterStart = now.minusDays(90);
                case "12months" -> filterStart = yearStart;
                case "all" -> filterStart = null;
                default -> { }
            }
        }

        LocalDateTime fStart = filterStart;
        LocalDateTime fEnd = filterEnd;

        List<Order> filteredOrders = filterOrders(allOrders, fStart, fEnd);

        // Previous period for comparison
        LocalDateTime prevStart = null;
        LocalDateTime prevEnd = fStart;
        if (fStart != null) {
            long days = switch (period != null ? period : "") {
                case "90days" -> 90L;
                case "12months" -> 365L;
                default -> 30L;
            };
            if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
                prevStart = fStart.minusDays(java.time.temporal.ChronoUnit.DAYS.between(fStart.toLocalDate(), fEnd != null ? fEnd.toLocalDate() : now.toLocalDate()));
            } else {
                prevStart = fStart.minusDays(days);
            }
        }
        List<Order> previousOrders = filterOrders(allOrders, prevStart, prevEnd);

        BigDecimal totalRevenue = sumRevenue(filteredOrders);
        BigDecimal prevRevenue = sumRevenue(previousOrders);
        long totalOrders = filteredOrders.size();
        long prevTotalOrders = previousOrders.size();

        long ordersThisMonth = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(monthStart))
                .count();

        long totalCustomers = allUsers.stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .count();

        long activeProducts = allProducts.stream()
                .filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE)
                .count();

        BigDecimal conversionRate = totalCustomers > 0
                ? BigDecimal.valueOf(totalOrders * 100.0 / totalCustomers).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal prevConversionRate = totalCustomers > 0
                ? BigDecimal.valueOf(prevTotalOrders * 100.0 / totalCustomers).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal avgOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal prevAvgOrderValue = prevTotalOrders > 0
                ? prevRevenue.divide(BigDecimal.valueOf(prevTotalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long returnedOrCancelled = filteredOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED
                        || o.getStatus() == Order.OrderStatus.RETURNED)
                .count();
        BigDecimal returnRate = totalOrders > 0
                ? BigDecimal.valueOf(returnedOrCancelled * 100.0 / totalOrders).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long prevReturnedOrCancelled = previousOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED
                        || o.getStatus() == Order.OrderStatus.RETURNED)
                .count();
        BigDecimal prevReturnRate = prevTotalOrders > 0
                ? BigDecimal.valueOf(prevReturnedOrCancelled * 100.0 / prevTotalOrders).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long abandonedThisMonth = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING
                        || o.getStatus() == Order.OrderStatus.CANCELLED)
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(monthStart))
                .count();
        BigDecimal abandonmentRate = ordersThisMonth + abandonedThisMonth > 0
                ? BigDecimal.valueOf(abandonedThisMonth * 100.0 / (ordersThisMonth + abandonedThisMonth))
                        .setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long prevAbandoned = previousOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING
                        || o.getStatus() == Order.OrderStatus.CANCELLED)
                .count();
        BigDecimal prevAbandonmentRate = prevTotalOrders + prevAbandoned > 0
                ? BigDecimal.valueOf(prevAbandoned * 100.0 / (prevTotalOrders + prevAbandoned))
                        .setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<Map<String, Object>> monthlyTrend = getMonthlyTrend(filteredOrders, yearStart);
        List<Map<String, Object>> weeklyTrend = getWeeklyTrend(filteredOrders);
        List<Map<String, Object>> categoryBreakdown = getCategoryBreakdown(allProducts, filteredOrders);
        List<Map<String, Object>> orderStatusBreakdown = getOrderStatusBreakdown(filteredOrders);
        List<Map<String, Object>> paymentStatusBreakdown = getPaymentStatusBreakdown(filteredOrders);

        List<Map<String, Object>> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::mapRecentOrder)
                .toList();

        List<Map<String, Object>> bestSellers = allProducts.stream()
                .filter(p -> p.getTotalSales() != null && p.getTotalSales() > 0)
                .sorted(Comparator.comparing((Product p) -> p.getTotalSales() == null ? 0 : p.getTotalSales()).reversed())
                .limit(5)
                .map(this::mapBestSeller)
                .toList();

        Map<String, Object> kpis = new LinkedHashMap<>();
        kpis.put("conversionRate", buildPercentKpi("Conversion Rate", conversionRate, prevConversionRate, false));
        kpis.put("avgOrderValue", buildLkrKpi("Avg Order Value", avgOrderValue, prevAvgOrderValue));
        kpis.put("returnRate", buildPercentKpi("Return Rate", returnRate, prevReturnRate, true));
        kpis.put("cartAbandonment", buildPercentKpi("Cart Abandonment", abandonmentRate, prevAbandonmentRate, true));

        double revenueGrowth = calcGrowth(totalRevenue.doubleValue(), prevRevenue.doubleValue());
        double orderGrowth = calcGrowth(totalOrders, prevTotalOrders);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kpis", kpis);
        result.put("monthlyTrend", monthlyTrend);
        result.put("weeklyTrend", weeklyTrend);
        result.put("categoryBreakdown", categoryBreakdown);
        result.put("orderStatusBreakdown", orderStatusBreakdown);
        result.put("paymentStatusBreakdown", paymentStatusBreakdown);
        result.put("trafficSources", orderStatusBreakdown);
        result.put("recentOrders", recentOrders);
        result.put("bestSellers", bestSellers);
        result.put("totalRevenue", totalRevenue);
        result.put("totalOrders", totalOrders);
        result.put("ordersThisMonth", ordersThisMonth);
        result.put("activeProducts", activeProducts);
        result.put("totalProducts", (long) allProducts.size());
        result.put("totalCustomers", totalCustomers);
        result.put("avgOrderValue", avgOrderValue);
        result.put("totalRevenueLkr", "LKR " + formatLkrFull(totalRevenue));
        result.put("revenueGrowth", revenueGrowth);
        result.put("orderGrowth", orderGrowth);
        result.put("prevTotalRevenue", prevRevenue);
        result.put("prevTotalOrders", prevTotalOrders);
        result.put("period", period != null ? period : "30days");

        return result;
    }

    public Map<String, Object> getOverviewStats() {
        List<Order> allOrders = orderRepository.findAll();
        List<Product> allProducts = productRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRevenue", allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stats.put("totalOrders", (long) allOrders.size());
        stats.put("totalCustomers", allUsers.stream().filter(u -> u.getRole() == User.Role.CUSTOMER).count());
        stats.put("activeProducts", allProducts.stream().filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE).count());
        stats.put("totalProducts", (long) allProducts.size());
        return stats;
    }

    private List<Order> filterOrders(List<Order> orders, LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> start == null || !o.getCreatedAt().isBefore(start))
                .filter(o -> end == null || o.getCreatedAt().isBefore(end))
                .toList();
    }

    private BigDecimal sumRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double calcGrowth(double current, double previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return Math.round((current - previous) / previous * 1000.0) / 10.0;
    }

    private Map<String, Object> buildPercentKpi(String label, BigDecimal current, BigDecimal previous, boolean lowerIsBetter) {
        double diff = current.subtract(previous).doubleValue();
        boolean up = lowerIsBetter ? diff <= 0 : diff >= 0;
        String change = String.format("%+.1f%%", diff);
        return Map.of("label", label, "value", current + "%", "change", change, "up", up);
    }

    private Map<String, Object> buildLkrKpi(String label, BigDecimal current, BigDecimal previous) {
        BigDecimal diff = current.subtract(previous);
        boolean up = diff.compareTo(BigDecimal.ZERO) >= 0;
        String change = diff.compareTo(BigDecimal.ZERO) == 0
                ? "No change"
                : (up ? "+" : "-") + "LKR " + formatLkrFull(diff.abs());
        return Map.of(
                "label", label,
                "value", "LKR " + formatLkrFull(current),
                "change", change,
                "up", up
        );
    }

    private Map<String, Object> mapRecentOrder(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("orderNumber", o.getOrderNumber());
        m.put("customerName", o.getUser() == null ? "Unknown" : o.getUser().getName());
        m.put("status", o.getStatus() == null ? null : o.getStatus().name());
        m.put("totalAmount", o.getTotalAmount());
        m.put("date", o.getCreatedAt());
        return m;
    }

    private Map<String, Object> mapBestSeller(Product p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("price", p.getPrice());
        m.put("totalSales", p.getTotalSales());
        m.put("stock", p.getStock());
        m.put("revenue", p.getPrice() != null && p.getTotalSales() != null
                ? p.getPrice().multiply(BigDecimal.valueOf(p.getTotalSales()))
                : BigDecimal.ZERO);
        return m;
    }

    private List<Map<String, Object>> getMonthlyTrend(List<Order> allOrders, LocalDateTime yearStart) {
        Map<String, long[]> monthly = new LinkedHashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String m : months) monthly.put(m, new long[]{0, 0});

        for (Order o : allOrders) {
            if (o.getCreatedAt() == null || o.getCreatedAt().isBefore(yearStart)) continue;
            String key = months[o.getCreatedAt().getMonthValue() - 1];
            long[] v = monthly.get(key);
            v[0]++;
            if (o.getTotalAmount() != null) {
                v[1] += o.getTotalAmount().longValue();
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String m : months) {
            long[] v = monthly.get(m);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("label", m);
            entry.put("orders", v[0]);
            entry.put("revenue", v[1]);
            result.add(entry);
        }
        return result;
    }

    private List<Map<String, Object>> getWeeklyTrend(List<Order> allOrders) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        Map<LocalDate, long[]> daily = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {
            daily.put(weekAgo.plusDays(i), new long[]{0, 0});
        }

        for (Order o : allOrders) {
            if (o.getCreatedAt() == null) continue;
            LocalDate d = o.getCreatedAt().toLocalDate();
            if (d.isBefore(weekAgo) || d.isAfter(today)) continue;
            long[] v = daily.get(d);
            if (v != null) {
                v[0]++;
                if (o.getTotalAmount() != null) {
                    v[1] += o.getTotalAmount().longValue();
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, long[]> e : daily.entrySet()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("label", e.getKey().getDayOfWeek().name().substring(0, 3));
            entry.put("orders", e.getValue()[0]);
            entry.put("revenue", e.getValue()[1]);
            result.add(entry);
        }
        return result;
    }

    private List<Map<String, Object>> getCategoryBreakdown(List<Product> allProducts, List<Order> allOrders) {
        Map<String, BigDecimal> categoryRevenue = new LinkedHashMap<>();
        for (Order o : allOrders) {
            if (o.getOrderItems() == null) continue;
            o.getOrderItems().forEach(item -> {
                if (item.getProduct() != null && item.getProduct().getCategory() != null) {
                    String catName = item.getProduct().getCategory().getName();
                    if (catName == null) return;
                    BigDecimal lineTotal = item.getTotal() != null
                            ? item.getTotal()
                            : (item.getUnitPrice() != null && item.getQuantity() != null
                                    ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                                    : BigDecimal.ZERO);
                    categoryRevenue.merge(catName, lineTotal, BigDecimal::add);
                }
            });
        }

        BigDecimal totalRevenue = categoryRevenue.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> result = new ArrayList<>();
        String[] colors = {"bg-amber-400", "bg-stone-700", "bg-amber-200", "bg-stone-300", "bg-stone-100"};
        int idx = 0;
        for (Map.Entry<String, BigDecimal> e : categoryRevenue.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .toList()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", e.getKey());
            int pct = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? e.getValue().multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            entry.put("pct", pct);
            entry.put("revenue", e.getValue());
            entry.put("color", colors[idx % colors.length]);
            result.add(entry);
            idx++;
        }
        return result;
    }

    private List<Map<String, Object>> getOrderStatusBreakdown(List<Order> orders) {
        Map<String, Long> statusCounts = orders.stream()
                .filter(o -> o.getStatus() != null)
                .collect(Collectors.groupingBy(o -> formatStatusLabel(o.getStatus().name()), Collectors.counting()));

        long total = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) return List.of();

        return statusCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(6)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("source", e.getKey());
                    m.put("visits", e.getValue());
                    m.put("pct", Math.round(e.getValue() * 100.0 / total));
                    return m;
                })
                .toList();
    }

    private List<Map<String, Object>> getPaymentStatusBreakdown(List<Order> orders) {
        Map<String, Long> counts = orders.stream()
                .filter(o -> o.getPaymentStatus() != null)
                .collect(Collectors.groupingBy(o -> formatStatusLabel(o.getPaymentStatus().name()), Collectors.counting()));

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) return List.of();

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("status", e.getKey());
                    m.put("count", e.getValue());
                    m.put("pct", Math.round(e.getValue() * 100.0 / total));
                    return m;
                })
                .toList();
    }

    private String formatStatusLabel(String status) {
        return Arrays.stream(status.toLowerCase().split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    private String formatLkrFull(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP)
                .toString()
                .replaceAll("(\\d)(?=(\\d{3})+(?!\\d))", "$1,");
    }
}
