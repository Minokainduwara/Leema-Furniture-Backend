package com.example.demo.controller.Admin;

import com.example.demo.service.AdminAnalyticsService;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(adminAnalyticsService.getAnalytics(period, from, to));
    }

    @GetMapping("/report/{type}")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable String type,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        byte[] reportData = reportService.generateReport(type, period, from, to);
        String filename = reportService.getFilename(type);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(reportData);
    }
}