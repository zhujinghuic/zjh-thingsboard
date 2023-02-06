package org.thingsboard.server.transport.ps.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 地址参数
 */
@Data
public class PsPakInfo {
    protected Integer address;
    protected String param;
    protected String unit;
    protected String scheme;
    protected Integer registerNum;
    protected Integer decimalPlaces;
}
