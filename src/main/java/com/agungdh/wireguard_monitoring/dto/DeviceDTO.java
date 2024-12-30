package com.agungdh.wireguard_monitoring.dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

public record DeviceDTO(String publicKey, String presharedKey, String endpoint, Instant latestHandshake, BigInteger transferRx,
                        BigInteger transferTx, List<String> allowedIps) {
}