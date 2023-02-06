package org.thingsboard.server.transport.ps.components.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.thingsboard.server.transport.ps.components.producer.PsProtocolProducer;

/**
 * 行为
 * @param <T>
 */
public class PsPackageProcess<T extends PsProtocolProducer> implements PsPackageProcessInterface {
    protected T t;

    public PsPackageProcess(T t) {
        this.t = t;
        this.produceProtocolObj();
    }

    @Override
    public void produceProtocolObj() {
        t.produceProtocolObj();
    }


    public T getProducer() {
        return t;
    }

    @Override
    public void action() throws JsonProcessingException {
        t.convert();
        t.process();
        t.replyPackage();
    }
}
