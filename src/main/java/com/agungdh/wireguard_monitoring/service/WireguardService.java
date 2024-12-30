package com.agungdh.wireguard_monitoring.service;

import com.agungdh.wireguard_monitoring.dto.ClientDTO;
import com.agungdh.wireguard_monitoring.dto.DeviceDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
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
        String[] command = { "sudo", "bash", "wg-json.sh" };

        StringBuilder output = getStringBuilder(command);

        // Read JSON file and parse into JsonNode
        JsonNode jsonNode = objectMapper.readTree(output.toString());

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
        File file = new File("/etc/wireguard/wg0.conf");

        List<ClientDTO> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("###")) {
                    String client = line.replace("### Client ", "");

                    reader.readLine();
                    String publicKey = reader.readLine().replace("PublicKey = ", "");
                    String presharedKey = reader.readLine().replace("PresharedKey = ", "");

                    ClientDTO clientDTO = new ClientDTO(client, publicKey, presharedKey, null);

                    clients.add(clientDTO);
                }
            }
        }

        return clients;
    }

    private static StringBuilder getStringBuilder(String[] command) throws IOException {
        // Start the process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Capture process output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        return output;
    }

}
