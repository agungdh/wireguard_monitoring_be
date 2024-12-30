package com.agungdh.wireguard_monitoring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/wireguard")
public class WireguardMonitoringController {
    @GetMapping
    public void stat()  throws Exception{
        File file = new File("/home/debian/repo/Wireguard Monitoring/sample.json");

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Read JSON file and parse into JsonNode
        JsonNode jsonNode = objectMapper.readTree(file);

        // Get wg0
        JsonNode wg0Node = jsonNode.get("wg0");

        // Get peersNode
        JsonNode peersNode = wg0Node.get("peers");

        Iterator<Map.Entry<String, JsonNode>> fields = peersNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            System.out.println("key: " + key + " value: " + value);
        }

//        // Access specific fields (example)
//        if (jsonNode.has("wg0")) {
//            System.out.println("Value of 'key': " + jsonNode.get("wg0").toPrettyString());
//        }
    }
}
