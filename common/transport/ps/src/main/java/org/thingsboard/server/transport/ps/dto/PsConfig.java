package org.thingsboard.server.transport.ps.dto;

import lombok.Data;

import java.util.List;

@Data
public class PsConfig extends PsPakInfo {
    private List<PsRefCoilConfig> refs;
}
