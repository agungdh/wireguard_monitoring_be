package com.agungdh.wireguard_monitoring.controller;

import com.agungdh.wireguard_monitoring.dto.HelloDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/")
public class MainController {
    @GetMapping
    public ResponseEntity<HelloDTO> hello() {
        HelloDTO hello = new HelloDTO("Hello, World!", Instant.now());

        return ResponseEntity.ok(hello);
    }
}
