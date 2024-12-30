package com.agungdh.wireguard_monitoring.controller;

import com.agungdh.wireguard_monitoring.dto.DeviceDTO;
import com.agungdh.wireguard_monitoring.service.WireguardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/wireguard")
@RequiredArgsConstructor
public class WireguardMonitoringController {
    private final WireguardService wireguardService;

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() throws Exception {
        wireguardService.getClient();
        return ResponseEntity.ok(wireguardService.getStat());
    }
}
