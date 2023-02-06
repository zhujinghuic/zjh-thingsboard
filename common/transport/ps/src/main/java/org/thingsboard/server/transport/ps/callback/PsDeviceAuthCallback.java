package org.thingsboard.server.transport.ps.callback;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.transport.TransportContext;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.common.transport.auth.SessionInfoCreator;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import org.thingsboard.server.gen.transport.TransportProtos;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class PsDeviceAuthCallback implements TransportServiceCallback<ValidateDeviceCredentialsResponse> {
    private final TransportContext transportContext;
    private final Consumer<TransportProtos.SessionInfoProto> onSuccess;

    public PsDeviceAuthCallback(TransportContext transportContext, Consumer<TransportProtos.SessionInfoProto> onSuccess) {
        this.transportContext = transportContext;
        this.onSuccess = onSuccess;
    }

    @Override
    public void onSuccess(ValidateDeviceCredentialsResponse msg) {
        if (msg.hasDeviceInfo()) {
            onSuccess.accept(SessionInfoCreator.create(msg, transportContext, UUID.randomUUID()));
        } else {
           log.info("接收不存在的设备");
        }
    }

    @Override
    public void onError(Throwable e) {
        log.warn("Failed to process request", e);
    }
}
