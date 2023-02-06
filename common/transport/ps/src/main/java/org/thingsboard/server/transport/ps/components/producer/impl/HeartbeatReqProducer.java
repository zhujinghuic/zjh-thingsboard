package org.thingsboard.server.transport.ps.components.producer.impl;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.transport.util.CrcUtils;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.components.model.inherits.read.HeartbeatReqPackage;
import org.thingsboard.server.transport.ps.components.producer.PsProtocolProducer;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

@Slf4j
public class HeartbeatReqProducer extends PsProtocolProducer<HeartbeatReqPackage> {

    private PsTransportContext psTransportContext;

    public HeartbeatReqProducer(String msg, ProtocolTypeEnum protocolType,
                                ChannelHandlerContext ctx, PsTransportContext psTransportContext) {
        super(msg, protocolType, ctx);
        this.psTransportContext = psTransportContext;
    }

    @Override
     public void produceProtocolObj() {
        this.t = new HeartbeatReqPackage(msg, protocolType);
    }

    @Override
    public void replyPackage() {
        ByteBuf byteValue = Unpooled.buffer();
        String hexStrPackage = new StringBuffer()
                .append(t.getPsProtocolPrefix())
                .append(ProtocolTypeEnum.ANS_ACK.getCode())
                .toString();
        hexStrPackage = new StringBuffer()
                .append(hexStrPackage)
                .append(CrcUtils.yihuo(hexStrPackage))
                .toString();
        byte[] bytes = HexUtil.decodeHex(hexStrPackage);
        byteValue.writeBytes(bytes);// 对接需要16进制
        ctx.writeAndFlush(byteValue);
        log.info("回复客户端-{}-报文-{}", ctx.channel().remoteAddress().toString(), hexStrPackage);
    }

    @Override
    public void process() throws JsonProcessingException {
        psTransportContext.channelRegistered(t.getDeviceUniqueFlag(), ctx);
    }
}
