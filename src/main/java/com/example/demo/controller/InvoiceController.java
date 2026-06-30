package com.example.demo.controller;

import com.example.demo.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{orderId}/download")
    public ResponseEntity<InputStreamResource>
    downloadInvoice(
            @PathVariable Integer orderId
    ) {

        ByteArrayInputStream invoice =
                invoiceService.generateInvoice(orderId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                "Content-Disposition",
                "inline; filename=invoice.pdf"
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        new InputStreamResource(invoice)
                );
    }
}