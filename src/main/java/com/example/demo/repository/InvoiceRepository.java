package com.example.demo.repository;

import com.example.demo.entity.Invoice;
import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Integer> {

    Optional<Invoice> findByOrder(Order order);

    Optional<Invoice> findByInvoiceNumber(
            String invoiceNumber
    );
}