package org.thingsboard.server.transport.ps.components.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoContent {
    private int num;
    private String content;
}
