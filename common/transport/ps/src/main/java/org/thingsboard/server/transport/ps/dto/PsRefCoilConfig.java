package org.thingsboard.server.transport.ps.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PsRefCoilConfig {
    private Integer address;
    private String param;
    private Map<String, String> keyVal;
}
