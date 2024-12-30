package com.agungdh.wireguard_monitoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ClientDTO(String client, @JsonIgnore String publicKey, @JsonIgnore String presharedKey, DeviceDTO stat) {
}
