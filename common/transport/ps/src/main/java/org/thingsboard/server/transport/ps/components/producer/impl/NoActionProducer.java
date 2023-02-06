package org.thingsboard.server.transport.ps.components.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.transport.ps.components.model.PsProtocol;
import org.thingsboard.server.transport.ps.components.model.inherits.read.NoActionPackage;
import org.thingsboard.server.transport.ps.components.producer.PsProtocolProducer;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

@Slf4j
public class NoActionProducer extends PsProtocolProducer<PsProtocol> {

    public NoActionProducer(String msg, ProtocolTypeEnum protocolType) {
        super(msg, protocolType);
    }

    @Override
    public void produceProtocolObj() {
        this.t = new NoActionPackage(msg, protocolType);
    }


}
