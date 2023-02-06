package org.thingsboard.server.transport.szy206.encoder.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author CaoLongMin
 * @version 1.0
 * @description TODO
 * @since 2022/6/23
 */
@Data
public class Szy206InfoDTO {

    /**
     * 开始字符串
     */
    private String startStr;
    /**
     * 监测类型
     */
    private String dataType;
    /**
     * 遥测站点地址
     */
    private String stationAddr;
    /**
     * 应用层功能码
     */
    private String functionCode;
    /**
     * 正文内容
     */
    private String content;
    /**
     * crc校验码
     */
    private String crcCode;
    /**
     * 结束字符
     */
    private String endStr;

    /**
     * 是否为心跳数据
     */
    private Boolean isHeartbeat;


    /**
     * 通讯时间
     */
    String dataTime;
    /**
     * 设备id
     */
    String deviceId;
    /**
     * 监测类型
     */
    String deviceIndex;
    /**
     * 报文正文内容
     */
    Map<String, Object> mainBody;

    /**
     * 报文正文说明
     */
    Map<String, Object> mainHead;
}
