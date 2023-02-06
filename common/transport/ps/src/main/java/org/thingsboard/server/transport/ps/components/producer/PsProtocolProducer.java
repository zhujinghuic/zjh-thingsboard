package org.thingsboard.server.transport.ps.components.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.thingsboard.server.transport.ps.components.model.PsProtocol;
import org.thingsboard.server.transport.ps.components.model.ReadPsProtocol;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 生产
 * @param <T>
 */
public abstract class PsProtocolProducer<T extends PsProtocol> {

    protected T t;
    protected String msg;
    protected ProtocolTypeEnum protocolType;
    protected ChannelHandlerContext ctx;

    public PsProtocolProducer(String msg, ProtocolTypeEnum protocolType, ChannelHandlerContext ctx) {
        this.msg = msg;
        this.protocolType = protocolType;
        this.ctx = ctx;
    }

    public PsProtocolProducer(String msg, ProtocolTypeEnum protocolType) {
        this.msg = msg;
        this.protocolType = protocolType;
    }

    public PsProtocolProducer() {
    }

    /**
     * 生成帧对象
     */
    public abstract void produceProtocolObj();

    /**
     * 接收到帧后转换
     */
    public void convert() {
    }

    /**
     * 接收到帧后处理
     */
    public void process() throws JsonProcessingException {
    }

    /**
     * 接收到帧后应答
     */
    public void replyPackage() {
    }

    public String getMsg() {
        return msg;
    }

    public T getProtocol() {
        return t;
    }
}
