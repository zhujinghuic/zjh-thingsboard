package org.thingsboard.server.transport.ps.components.action;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 行为
 * @param
 */
public interface PsPackageProcessInterface {
    /**
     * 创建协议帧对象
     */
    void produceProtocolObj();
    /**
     * 行为
     * @throws JsonProcessingException
     */
    void action() throws JsonProcessingException;
}
