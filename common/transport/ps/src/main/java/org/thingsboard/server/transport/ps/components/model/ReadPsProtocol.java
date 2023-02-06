package org.thingsboard.server.transport.ps.components.model;

import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 读帧
 */
public abstract class ReadPsProtocol extends PsProtocol {

    public ReadPsProtocol(String msg, ProtocolTypeEnum type) {
        super(type);
        setDeviceUniqueFlag(msg.substring(16, 28));
        setServerUniqueFlag(msg.substring(30, 42));
        setSendPsProtocolPrefix();
    }

}
