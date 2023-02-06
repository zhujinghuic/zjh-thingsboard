package org.thingsboard.server.transport.ps.enums;

import java.util.Arrays;

public enum ProtocolTypeEnum {

    /*33注册帧*/
    REGISTER("33"),
    /*2C功能码*/
    CONTENT("2C"),
    /*55应答帧*/
    ANS_ACK("55"),
    /*05写寄存器*/
    WRITE("05"),
    /*拍照*/
    PHOTOGRAPH("EB"),

    ;


    private String code;

    ProtocolTypeEnum(String code) {
        this.code = code;
    }

    public static ProtocolTypeEnum get(String code) {
        return Arrays.stream(ProtocolTypeEnum.values()).filter(v -> v.getCode().equals(code)).findAny().orElse(PHOTOGRAPH);
    }

    public String getCode() {
        return code;
    }


}
