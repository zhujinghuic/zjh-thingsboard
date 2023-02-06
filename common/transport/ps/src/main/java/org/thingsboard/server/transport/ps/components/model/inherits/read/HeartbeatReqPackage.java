package org.thingsboard.server.transport.ps.components.model.inherits.read;

import lombok.experimental.Accessors;
import org.thingsboard.server.transport.ps.components.model.ReadPsProtocol;
import org.thingsboard.server.transport.ps.enums.PackTypeEnum;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 心跳帧
 */
@Accessors(chain = true)
public class HeartbeatReqPackage extends ReadPsProtocol {

    public HeartbeatReqPackage(String msg, ProtocolTypeEnum type) {
        super(msg, type);
    }


    @Override
    public void setSendPsProtocolPrefix() {
        this.psProtocolPrefix = new StringBuffer()
                .append(SYS_IDENTIFIER)
                .append("0017") // 帧长度为17
                .append(PACK_NUM)
                .append(PackTypeEnum.link.getCode())
                .append(DOT_LEN)
                .append(serverUniqueFlag)
                .append(DOT_LEN)
                .append(deviceUniqueFlag)
                .toString();
    }
}
