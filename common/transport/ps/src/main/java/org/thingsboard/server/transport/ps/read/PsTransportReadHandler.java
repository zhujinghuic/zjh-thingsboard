/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.transport.ps.read;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.transport.SessionMsgListener;
import org.thingsboard.server.common.transport.util.CrcUtils;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.components.action.PsPackageProcess;
import org.thingsboard.server.transport.ps.encoder.PsDecoder;

import java.util.UUID;

/**
 * @author Andrew Shvayka
 */
@Slf4j
@ChannelHandler.Sharable
public class PsTransportReadHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<? super Void>>, SessionMsgListener {
    private final PsTransportContext context;

    public PsTransportReadHandler(PsTransportContext context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务器读取客户端-{}-上报的报文-{}", ctx.channel().remoteAddress().toString(), msg);
        ByteBuf buf = (ByteBuf) msg;
        String strMsg = CrcUtils.bytesToHexString(buf);
        PsDecoder psDecoder = new PsDecoder(context, ctx);
        PsPackageProcess psPackageAction = psDecoder.decoder(strMsg);
        psPackageAction.action();
        log.info("解析PS协议数据报文类型是-{},内容是-{}", psPackageAction.getProducer().getProtocol().getProtocolType().name(), strMsg);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端-{}-建立连接", ctx.channel().remoteAddress().toString());
//        context.channelRegistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        log.info("客户端-{}-断开连接", ctx.channel().remoteAddress().toString());
        context.channelUnregistered(ctx);
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush 是 write + flush
        //将数据写入到缓存，并刷新
        //一般讲，我们对这个发送的数据进行编码
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵1", CharsetUtil.UTF_8));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        context.channelUnregistered(ctx);
    }

    //处理异常, 一般是需要关闭通道

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通讯发生异常，异常原因-{}", cause.getStackTrace());
//        ctx.close();
    }


    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {

    }

    @Override
    public void onGetAttributesResponse(TransportProtos.GetAttributeResponseMsg getAttributesResponse) {

    }

    @Override
    public void onAttributeUpdate(UUID sessionId, TransportProtos.AttributeUpdateNotificationMsg attributeUpdateNotification) {

    }

    @Override
    public void onRemoteSessionCloseCommand(UUID sessionId, TransportProtos.SessionCloseNotificationProto sessionCloseNotification) {

    }

    @Override
    public void onToDeviceRpcRequest(UUID sessionId, TransportProtos.ToDeviceRpcRequestMsg toDeviceRequest) {

    }

    @Override
    public void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse) {

    }

    @Override
    public void onDeviceDeleted(DeviceId deviceId) {

    }

}
