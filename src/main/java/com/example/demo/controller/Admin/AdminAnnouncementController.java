package com.example.demo.controller.Admin;

import com.example.demo.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @RequestBody Map<String, String> request) {
        String title = request.get("title");
        String message = request.get("message");
        String audience = request.get("audience");
        
        int recipients = announcementService.sendAnnouncement(title, message, audience);
        
        return ResponseEntity.ok(Map.of("recipients", recipients));
    }
}