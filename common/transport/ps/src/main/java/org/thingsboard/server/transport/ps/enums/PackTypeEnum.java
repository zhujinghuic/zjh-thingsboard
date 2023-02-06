package org.thingsboard.server.transport.ps.enums;

/**
 * 帧类型
 */
public enum PackTypeEnum {
    /*数据帧*/
    digital("01"),
    /*链路帧*/
    link("06");

    private String code;

    PackTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
