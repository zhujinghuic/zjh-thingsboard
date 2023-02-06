package org.thingsboard.server.transport.ps.components.model.inherits.read;

import lombok.experimental.Accessors;
import org.thingsboard.server.transport.ps.components.model.ReadPsProtocol;
import org.thingsboard.server.transport.ps.enums.PackTypeEnum;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 心跳帧
 */
@Accessors(chain = true)
public class NoActionPackage extends ReadPsProtocol {


    public NoActionPackage(String msg, ProtocolTypeEnum protocolType) {
        super(msg, protocolType);
    }


}
