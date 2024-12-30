package com.agungdh.wireguard_monitoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

public record DeviceDTO(@JsonIgnore String publicKey, @JsonIgnore String presharedKey, String endpoint, Instant latestHandshake, BigInteger transferRx,
                        BigInteger transferTx, List<String> allowedIps) {
}