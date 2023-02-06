package org.thingsboard.server.transport.ps.components.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public abstract class PsProtocol {

    /*平升协议-系统识别码*/
    public static final String SYS_IDENTIFIER = "123456";
    /*平升协议-包序号*/
    public static final String PACK_NUM = "80";
    /*平升协议-上行或下行的地址长度（一般为11位地址，若地址长度有变化，该值也会变化）*/
    public static final String DOT_LEN = "0B";
    /*rtu地址*/
    public static final String RTU_ADDRESS = "65";

    /*帧类型*/
    protected ProtocolTypeEnum protocolType;
    /*写功能ps协议前缀*/
    protected String psProtocolPrefix;
    /*rtu设备唯一标识*/
    protected String deviceUniqueFlag;
    /*中心服务唯一标识*/
    protected String serverUniqueFlag;

    public PsProtocol(ProtocolTypeEnum type) {
        setProtocolType(type);
    }

    /**
     * 设置发送帧的ps前缀
     */
    public void setSendPsProtocolPrefix() {};


}
