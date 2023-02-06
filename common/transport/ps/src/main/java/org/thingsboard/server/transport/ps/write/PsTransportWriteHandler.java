///**
// * Copyright © 2016-2022 The Thingsboard Authors
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.thingsboard.server.transport.ps.write;
//
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelOutboundHandlerAdapter;
//import io.netty.channel.ChannelPromise;
//import io.netty.handler.codec.mqtt.MqttQoS;
//import io.netty.handler.ssl.SslHandler;
//import io.netty.util.CharsetUtil;
//import io.netty.util.concurrent.Future;
//import io.netty.util.concurrent.GenericFutureListener;
//import lombok.extern.slf4j.Slf4j;
//import org.thingsboard.server.common.data.device.profile.MqttTopics;
//import org.thingsboard.server.common.data.id.DeviceId;
//import org.thingsboard.server.common.transport.SessionMsgListener;
//import org.thingsboard.server.common.transport.TransportService;
//import org.thingsboard.server.gen.transport.TransportProtos;
//import org.thingsboard.server.queue.scheduler.SchedulerComponent;
//import org.thingsboard.server.transport.ps.PsTransportContext;
//
//import java.net.InetSocketAddress;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.regex.Pattern;
//
//import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;
//
///**
// * @author Andrew Shvayka
// */
//@Slf4j
//public class PsTransportWriteHandler extends ChannelOutboundHandlerAdapter implements GenericFutureListener<Future<? super Void>>, SessionMsgListener {
//
//    private final PsTransportContext context;
//    private final TransportService transportService;
//    private final SchedulerComponent scheduler;
//    private final SslHandler sslHandler;
//
//    public PsTransportWriteHandler(PsTransportContext context, SslHandler sslHandler) {
//        this.context = context;
//        this.transportService = context.getTransportService();
//        this.scheduler = context.getScheduler();
//        this.sslHandler = sslHandler;
//    }
//
//    @Override
//    public void read(ChannelHandlerContext ctx) throws Exception {
//    }
//
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵1", CharsetUtil.UTF_8));
//    }
//
//
//    @Override
//    public void operationComplete(Future<? super Void> future) throws Exception {
//
//    }
//
//    @Override
//    public void onGetAttributesResponse(TransportProtos.GetAttributeResponseMsg getAttributesResponse) {
//
//    }
//
//    @Override
//    public void onAttributeUpdate(UUID sessionId, TransportProtos.AttributeUpdateNotificationMsg attributeUpdateNotification) {
//
//    }
//
//    @Override
//    public void onRemoteSessionCloseCommand(UUID sessionId, TransportProtos.SessionCloseNotificationProto sessionCloseNotification) {
//
//    }
//
//    @Override
//    public void onToDeviceRpcRequest(UUID sessionId, TransportProtos.ToDeviceRpcRequestMsg toDeviceRequest) {
//
//    }
//
//    @Override
//    public void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse) {
//
//    }
//
//    @Override
//    public void onDeviceDeleted(DeviceId deviceId) {
//
//    }
//
//    public PsTransportContext getContext() {
//        return context;
//    }
//}
