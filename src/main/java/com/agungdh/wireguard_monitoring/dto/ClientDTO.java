package com.agungdh.wireguard_monitoring.dto;

public record ClientDTO(String client, String publicKey, String presharedKey) {
}
