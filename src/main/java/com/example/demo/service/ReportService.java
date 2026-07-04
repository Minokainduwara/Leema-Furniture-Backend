package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public byte[] generateReport(String type, String period, String from, String to) {
        return switch (type.toLowerCase()) {
            case "sales" -> generateSalesReport(period, from, to);
            case "customers" -> generateCustomerReport(period, from, to);
            case "inventory" -> generateInventoryReport(period, from, to);
            case "categories" -> generateCategoryReport(period, from, to);
            case "revenue" -> generateRevenueReport(period, from, to);
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
    }

    public String getFilename(String type) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return type.toLowerCase() + "-report-" + date + ".csv";
    }

    private List<Order> getFilteredOrders(String period, String from, String to) {
        List<Order> allOrders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

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
                case "12months" -> filterStart = now.withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                case "all" -> filterStart = null;
                default -> { }
            }
        }

        LocalDateTime fStart = filterStart;
        LocalDateTime fEnd = filterEnd;

        return allOrders.stream()
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> fStart == null || !o.getCreatedAt().isBefore(fStart))
                .filter(o -> fEnd == null || o.getCreatedAt().isBefore(fEnd))
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private byte[] toCsvBytes(String content) {
        byte[] body = content.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, result, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, result, UTF8_BOM.length, body.length);
        return result;
    }

    private byte[] generateSalesReport(String period, String from, String to) {
        List<Order> orders = getFilteredOrders(period, from, to);
        StringBuilder csv = new StringBuilder();

        csv.append("Leema Furnitures — Sales Report\n");
        csv.append("Currency: LKR\n");
        csv.append("Generated:,").append(nowLabel()).append("\n\n");

        csv.append("Order #,Customer,Date,Status,Total Amount (LKR),Payment Status,Payment Method\n");
        for (Order o : orders) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(o.getOrderNumber() != null ? o.getOrderNumber() : "#" + o.getId()),
                    escapeCsv(o.getUser() != null ? o.getUser().getName() : "Unknown"),
                    o.getCreatedAt() != null ? o.getCreatedAt().toLocalDate().toString() : "N/A",
                    o.getStatus() != null ? o.getStatus().name() : "N/A",
                    formatLkr(o.getTotalAmount()),
                    o.getPaymentStatus() != null ? o.getPaymentStatus().name() : "N/A",
                    o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "N/A"
            ));
        }

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        csv.append("\nSummary\n");
        csv.append("Total Orders,").append(orders.size()).append("\n");
        csv.append("Total Revenue (LKR),").append(formatLkr(totalRevenue)).append("\n");
        csv.append("Average Order Value (LKR),").append(formatLkr(orders.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP))).append("\n");

        return toCsvBytes(csv.toString());
    }

    private byte[] generateCustomerReport(String period, String from, String to) {
        List<Order> orders = getFilteredOrders(period, from, to);

        Map<String, List<Order>> customerOrders = orders.stream()
                .filter(o -> o.getUser() != null)
                .collect(Collectors.groupingBy(o -> o.getUser().getName() != null ? o.getUser().getName() : "Unknown"));

        StringBuilder csv = new StringBuilder();
        csv.append("Leema Furnitures — Customer Report\n");
        csv.append("Currency: LKR\n");
        csv.append("Generated:,").append(nowLabel()).append("\n\n");

        csv.append("Customer,Email,Total Orders,Total Spent (LKR),Avg Order (LKR),Last Order Date\n");

        for (Map.Entry<String, List<Order>> entry : customerOrders.entrySet()) {
            List<Order> customerOrderList = entry.getValue();
            BigDecimal totalSpent = customerOrderList.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String lastOrderDate = customerOrderList.stream()
                    .filter(o -> o.getCreatedAt() != null)
                    .max(Comparator.comparing(Order::getCreatedAt))
                    .map(o -> o.getCreatedAt().toLocalDate().toString())
                    .orElse("N/A");

            String email = customerOrderList.get(0).getUser() != null
                    ? (customerOrderList.get(0).getUser().getEmail() != null ? customerOrderList.get(0).getUser().getEmail() : "N/A")
                    : "N/A";

            BigDecimal avg = customerOrderList.isEmpty() ? BigDecimal.ZERO
                    : totalSpent.divide(BigDecimal.valueOf(customerOrderList.size()), 2, RoundingMode.HALF_UP);

            csv.append(String.format("%s,%s,%d,%s,%s,%s\n",
                    escapeCsv(entry.getKey()),
                    escapeCsv(email),
                    customerOrderList.size(),
                    formatLkr(totalSpent),
                    formatLkr(avg),
                    lastOrderDate
            ));
        }

        csv.append("\nSummary\n");
        csv.append("Total Customers,").append(customerOrders.size()).append("\n");
        csv.append("Report Generated,").append(nowLabel()).append("\n");

        return toCsvBytes(csv.toString());
    }

    private byte[] generateInventoryReport(String period, String from, String to) {
        List<Product> products = productRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("Leema Furnitures — Inventory Report\n");
        csv.append("Currency: LKR\n");
        csv.append("Generated:,").append(nowLabel()).append("\n\n");

        csv.append("SKU,Product Name,Category,Price (LKR),Stock,Status,Total Sales,Stock Value (LKR)\n");

        for (Product p : products) {
            BigDecimal price = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
            int stock = p.getStock() != null ? p.getStock() : 0;
            csv.append(String.format("%s,%s,%s,%s,%d,%s,%d,%s\n",
                    escapeCsv(p.getSku() != null ? p.getSku() : "N/A"),
                    escapeCsv(p.getName() != null ? p.getName() : "N/A"),
                    escapeCsv(p.getCategory() != null && p.getCategory().getName() != null ? p.getCategory().getName() : "N/A"),
                    formatLkr(price),
                    stock,
                    p.getStatus() != null ? p.getStatus().name() : "N/A",
                    p.getTotalSales() != null ? p.getTotalSales() : 0,
                    formatLkr(price.multiply(BigDecimal.valueOf(stock)))
            ));
        }

        long activeCount = products.stream().filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE).count();
        long lowStockCount = products.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0 && p.getStock() <= 5).count();
        long outOfStockCount = products.stream()
                .filter(p -> p.getStock() != null && p.getStock() == 0).count();

        csv.append("\nSummary\n");
        csv.append("Total Products,").append(products.size()).append("\n");
        csv.append("Active,").append(activeCount).append("\n");
        csv.append("Low Stock,").append(lowStockCount).append("\n");
        csv.append("Out of Stock,").append(outOfStockCount).append("\n");
        csv.append("Report Generated,").append(nowLabel()).append("\n");

        return toCsvBytes(csv.toString());
    }

    private byte[] generateCategoryReport(String period, String from, String to) {
        List<Order> orders = getFilteredOrders(period, from, to);
        Map<String, CategoryStats> stats = new LinkedHashMap<>();

        for (Order o : orders) {
            if (o.getOrderItems() == null) continue;
            o.getOrderItems().forEach(item -> {
                if (item.getProduct() == null || item.getProduct().getCategory() == null) return;
                String cat = item.getProduct().getCategory().getName();
                CategoryStats s = stats.computeIfAbsent(cat, k -> new CategoryStats());
                s.unitsSold += item.getQuantity() != null ? item.getQuantity() : 0;
                BigDecimal lineTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
                s.revenue = s.revenue.add(lineTotal);
                s.orderCount++;
            });
        }

        StringBuilder csv = new StringBuilder();
        csv.append("Leema Furnitures — Category Sales Report\n");
        csv.append("Currency: LKR\n");
        csv.append("Generated:,").append(nowLabel()).append("\n\n");
        csv.append("Category,Units Sold,Orders,Revenue (LKR),Share %\n");

        BigDecimal totalRevenue = stats.values().stream()
                .map(s -> s.revenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.entrySet().stream()
                .sorted((a, b) -> b.getValue().revenue.compareTo(a.getValue().revenue))
                .forEach(e -> {
                    int share = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().revenue.multiply(BigDecimal.valueOf(100))
                                    .divide(totalRevenue, 0, RoundingMode.HALF_UP).intValue()
                            : 0;
                    csv.append(String.format("%s,%d,%d,%s,%d%%\n",
                            escapeCsv(e.getKey()),
                            e.getValue().unitsSold,
                            e.getValue().orderCount,
                            formatLkr(e.getValue().revenue),
                            share
                    ));
                });

        csv.append("\nSummary\n");
        csv.append("Total Categories,").append(stats.size()).append("\n");
        csv.append("Total Revenue (LKR),").append(formatLkr(totalRevenue)).append("\n");

        return toCsvBytes(csv.toString());
    }

    private byte[] generateRevenueReport(String period, String from, String to) {
        List<Order> orders = getFilteredOrders(period, from, to);
        Map<String, BigDecimal> dailyRevenue = new TreeMap<>();

        for (Order o : orders) {
            if (o.getCreatedAt() == null || o.getTotalAmount() == null) continue;
            String day = o.getCreatedAt().toLocalDate().toString();
            dailyRevenue.merge(day, o.getTotalAmount(), BigDecimal::add);
        }

        StringBuilder csv = new StringBuilder();
        csv.append("Leema Furnitures — Revenue Report\n");
        csv.append("Currency: LKR\n");
        csv.append("Generated:,").append(nowLabel()).append("\n\n");
        csv.append("Date,Orders,Revenue (LKR)\n");

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> e : dailyRevenue.entrySet()) {
            long dayOrders = orders.stream()
                    .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().toString().equals(e.getKey()))
                    .count();
            totalRevenue = totalRevenue.add(e.getValue());
            csv.append(String.format("%s,%d,%s\n", e.getKey(), dayOrders, formatLkr(e.getValue())));
        }

        csv.append("\nSummary\n");
        csv.append("Total Days,").append(dailyRevenue.size()).append("\n");
        csv.append("Total Orders,").append(orders.size()).append("\n");
        csv.append("Total Revenue (LKR),").append(formatLkr(totalRevenue)).append("\n");
        csv.append("Average Daily Revenue (LKR),").append(formatLkr(dailyRevenue.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(dailyRevenue.size()), 2, RoundingMode.HALF_UP))).append("\n");

        return toCsvBytes(csv.toString());
    }

    private String formatLkr(BigDecimal amount) {
        if (amount == null) return "0.00";
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String nowLabel() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static class CategoryStats {
        int unitsSold;
        int orderCount;
        BigDecimal revenue = BigDecimal.ZERO;
    }
}
