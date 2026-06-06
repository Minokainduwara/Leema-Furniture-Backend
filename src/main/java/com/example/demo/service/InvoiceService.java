package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ByteArrayInputStream generateInvoice(
            Integer orderId
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        List<OrderItem> items =
                orderItemRepository.findByOrder(order);

        Document document = new Document();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();

            // =====================================
            // TITLE
            // =====================================

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    22,
                    Color.BLACK
            );

            Paragraph title = new Paragraph(
                    "LEEMA FURNITURE INVOICE",
                    titleFont
            );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            // =====================================
            // ORDER DETAILS
            // =====================================

            document.add(new Paragraph(
                    "Order Number: "
                            + order.getOrderNumber()
            ));

            document.add(new Paragraph(
                    "Customer: "
                            + order.getShippingAddress()
                            .getFullName()
            ));

            document.add(new Paragraph(
                    "Phone: "
                            + order.getShippingAddress()
                            .getPhoneNumber()
            ));

            document.add(new Paragraph(
                        "Address: "
                                + order.getShippingAddress()
                                .getStreetAddress()

                                + ", "

                                + (
                                order.getShippingAddress()
                                        .getApartmentSuite() != null
                                        &&
                                !order.getShippingAddress()
                                        .getApartmentSuite()
                                        .isBlank()

                                ? order.getShippingAddress()
                                        .getApartmentSuite() + ", "
                                : ""
                        )

                                + order.getShippingAddress()
                                .getCity()

                                + ", "

                                + order.getShippingAddress()
                                .getStateProvince()

                                + ", "

                                + order.getShippingAddress()
                                .getPostalCode()

                                + ", "

                                + order.getShippingAddress()
                                .getCountry()
                ));

            document.add(new Paragraph(" "));

            // =====================================
            // TABLE
            // =====================================

            PdfPTable table = new PdfPTable(4);

            table.setWidthPercentage(100);

            table.setWidths(new int[]{
                    4, 2, 2, 2
            });

            addTableHeader(table, "Product");
            addTableHeader(table, "Qty");
            addTableHeader(table, "Unit Price");
            addTableHeader(table, "Total");

            for (OrderItem item : items) {

                table.addCell(
                        item.getProduct().getName()
                );

                table.addCell(
                        String.valueOf(
                                item.getQuantity()
                        )
                );

                table.addCell(
                        "LKR "
                                + item.getUnitPrice()
                );

                table.addCell(
                        "LKR "
                                + item.getTotal()
                );
            }

            document.add(table);

            document.add(new Paragraph(" "));

            // =====================================
            // TOTALS
            // =====================================

            document.add(new Paragraph(
                    "Subtotal: LKR "
                            + order.getSubtotal()
            ));

            document.add(new Paragraph(
                    "Shipping: LKR "
                            + order.getShippingCost()
            ));

            document.add(new Paragraph(
                    "Total Amount: LKR "
                            + order.getTotalAmount()
            ));

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Payment Status: "
                            + order.getPaymentStatus()
            ));

            document.add(new Paragraph(
                    "Order Status: "
                            + order.getStatus()
            ));

            document.close();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to generate invoice"
            );
        }

        return new ByteArrayInputStream(
                out.toByteArray()
        );
    }

    // ============================================
    // TABLE HEADER
    // ============================================

    private void addTableHeader(
            PdfPTable table,
            String title
    ) {

        PdfPCell header = new PdfPCell();

        header.setBackgroundColor(Color.LIGHT_GRAY);

        header.setPhrase(
                new Phrase(title)
        );

        table.addCell(header);
    }
}