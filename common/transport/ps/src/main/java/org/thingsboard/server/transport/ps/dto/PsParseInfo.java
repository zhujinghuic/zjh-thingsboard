package org.thingsboard.server.transport.ps.dto;

import lombok.Data;

@Data
public class PsParseInfo extends PsPakInfo{
    private Object paramVal;
    private String remark;

}
