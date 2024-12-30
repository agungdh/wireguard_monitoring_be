package com.agungdh.wireguard_monitoring.controller;

import com.agungdh.wireguard_monitoring.dto.DeviceDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class WireguardMonitoringController {
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> stat() throws Exception {
        File file = new File("/home/agungdh/IdeaProjects/wireguard_monitoring_be/sample.json");

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Read JSON file and parse into JsonNode
        JsonNode jsonNode = objectMapper.readTree(file);

        // Get wg0
        JsonNode wg0Node = jsonNode.get("wg0");

        // Get peersNode
        JsonNode peersNode = wg0Node.get("peers");

        Iterator<Map.Entry<String, JsonNode>> fields = peersNode.fields();
        List<DeviceDTO> devices = new ArrayList<>();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String publicKey = field.getKey();
            JsonNode value = field.getValue();

            List<String> allowedIps = objectMapper.readValue(value.get("allowedIps").toString(), new TypeReference<>() {
            });

            DeviceDTO deviceDTO = new DeviceDTO(publicKey, value.get("presharedKey").asText(),
                    value.hasNonNull("endpoint") ? value.get("endpoint").asText() : null,
                    value.hasNonNull("latestHandshake") ? Instant.ofEpochSecond(value.get("latestHandshake").asLong()) : null,
                    value.hasNonNull("transferRx") ? new BigInteger(value.get("transferRx").asText()) : null,
                    value.hasNonNull("transferTx") ? new BigInteger(value.get("transferTx").asText()) : null,
                    allowedIps);

            devices.add(deviceDTO);
        }

        return ResponseEntity.ok(devices);
    }
}
