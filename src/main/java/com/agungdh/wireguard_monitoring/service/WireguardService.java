package com.agungdh.wireguard_monitoring.service;

import com.agungdh.wireguard_monitoring.dto.ClientDTO;
import com.agungdh.wireguard_monitoring.dto.DeviceDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class WireguardService {
    // Create ObjectMapper instance
    ObjectMapper objectMapper = new ObjectMapper();

    public WireguardService() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<DeviceDTO> getStat() throws Exception {
        File file = new File("/home/agungdh/IdeaProjects/wireguard_monitoring_be/sample.json");

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

        return devices;
    }

    public List<ClientDTO> getClient() throws Exception {
        File file = new File("/home/agungdh/IdeaProjects/wireguard_monitoring_be/wg0.config");

        List<ClientDTO> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("###")) {
                    String client = line.replace("### Client", "");

                    reader.readLine();
                    String publicKey = reader.readLine();
                    String presharedKey = reader.readLine();

                    ClientDTO clientDTO = new ClientDTO(client, publicKey, presharedKey);

                    clients.add(clientDTO);
                }
            }
        }

        return clients;
    }
}
