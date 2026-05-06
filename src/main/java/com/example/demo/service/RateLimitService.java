package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60;

    public void validateRequest(String key) {

        RequestInfo info = requestCounts.getOrDefault(key, new RequestInfo(0, Instant.now()));

        if (Instant.now().isAfter(info.timestamp.plusSeconds(WINDOW_SECONDS))) {
            info = new RequestInfo(0, Instant.now());
        }

        if (info.count >= MAX_REQUESTS) {
            throw new RuntimeException("Too many requests. Try again later.");
        }

        info.count++;
        requestCounts.put(key, info);
    }

    private static class RequestInfo {
        int count;
        Instant timestamp;

        RequestInfo(int count, Instant timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}