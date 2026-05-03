package com.example.demo.service;

import com.example.demo.entity.Return;
import com.example.demo.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReturnService {
    private final ReturnRepository returnRepository;

    public Return requestReturn(Return returnRequest) {
        return returnRepository.save(returnRequest);
    }

    public List<Return> getReturnsByUser(Integer userId) {
        return returnRepository.findByUserId(userId);
    }

    public Optional<Return> getReturnById(Integer id) {
        return returnRepository.findById(id);
    }

    public Return updateReturnStatus(Integer id, Return.ReturnStatus status) {
        return returnRepository.findById(id).map(r -> {
            r.setStatus(status);
            return returnRepository.save(r);
        }).orElse(null);
    }
}
