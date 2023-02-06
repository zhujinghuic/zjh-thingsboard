package org.thingsboard.server.transport.szy206.encoder.emums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CaoLongMin
 * @version 1.0
 * @description TODO
 * @since 2022/6/20
 */
@Getter
@AllArgsConstructor
public enum Szy206TypeEnum {
    //B2为数据类型（水位）
    //1) 0000B—雨量；
    //2) 0001B—水位；
    //3) 0010B—流量（水量）；
    //4) 0011B—流速；
    //5) 0100B—闸位；
    //6) 0101B—功率；
    //7) 0110B—气压；
    //8) 0111B—风速（风向）；
    //9) 1000B—水温；
    //10) 1001B—水质；
    //11) 1010B—土壤含水率；
    //12) 1011B—水压；
    //13) 1100B—1111B 备用。
    RAINFALL("B1","0000B","将雨量(mm)","降雨量"),
    WATER_LEVEL("B2","0001B","水位(m)","水位"),
    TRAFFIC("B3","0010B","流量（水量）","流量"),
    VELOCITY("B4","0011B","流速(m/s)","流速"),
    GATES("B5","0100B","闸位(m)","闸位"),
    POWER("B6","0101B","功率(kw)","功率"),
    AIR_PRESSURE("B7","0110B","气压(10²pa)","气压"),
    WIND_SPEED("B8","0111B","风速(m/s)","风速"),
    WATER_TEMPERATURE("B9","1000B","水温(℃)","水温"),
    WATER_QUALITY("BA","1001B","水质","水质"),
    MOISTURE_CONTENT("BB","1010B","土壤含水率","土壤含水率"),
    WATER_PRESSURE("BC","1011B","水压(kpa)","水压"),
    OTHERS("BD","1100B","电压(v)",""),

    //状态字段
    VOLTAGE("voltage","","电压状态","");

    /**
     * 关键词
     */
    private String key;

    /**
     * 关键词
     */
    private String code;
    /**
     * 描述
     */
    private String description;
    /**
     * 监测类型
     */
    private String deviceIndex;
}
