package com.example.demo.controller;

import com.example.demo.entity.Return;
import com.example.demo.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {
    private final ReturnService returnService;

    @PostMapping
    public ResponseEntity<Return> requestReturn(@RequestBody Return returnRequest) {
        return ResponseEntity.ok(returnService.requestReturn(returnRequest));
    }

    @GetMapping
    public ResponseEntity<List<Return>> getReturns(@RequestParam Integer userId) {
        return ResponseEntity.ok(returnService.getReturnsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Return> getReturnById(@PathVariable Integer id) {
        return returnService.getReturnById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<Return> updateReturnStatus(@PathVariable Integer id, @RequestParam Return.ReturnStatus status) {
        Return updated = returnService.updateReturnStatus(id, status);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
