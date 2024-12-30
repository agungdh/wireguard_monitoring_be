package com.agungdh.wireguard_monitoring.controller;

import com.agungdh.wireguard_monitoring.dto.ClientDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wireguard")
@RequiredArgsConstructor
public class WireguardMonitoringController {
    private final WireguardService wireguardService;

    @GetMapping
    public ResponseEntity<List<ClientDTO>> wireguard() throws Exception {
        List<DeviceDTO> devices = wireguardService.getStat();
        List<ClientDTO> clients = wireguardService.getClient();

        // Match and create new Client instances with the Device set
        List<ClientDTO> updatedClients = clients.stream()
                .map(client -> devices.stream()
                        .filter(device -> device.publicKey().equals(client.publicKey()))
                        .filter(device -> device.presharedKey().equals(client.presharedKey()))
                        .findFirst()
                        .map(device -> new ClientDTO(client.client(), client.publicKey(), client.presharedKey(), device))
                        .orElse(client)
                )
                .toList();


        return ResponseEntity.ok(updatedClients);
    }

    @GetMapping("/device")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() throws Exception {
        return ResponseEntity.ok(wireguardService.getStat());
    }

    @GetMapping("/client")
    public ResponseEntity<List<ClientDTO>> getAllClients() throws Exception {
        return ResponseEntity.ok(wireguardService.getClient());
    }
}
