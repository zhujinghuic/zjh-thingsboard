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
package org.thingsboard.server.transport.ps;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.transport.TransportContext;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ashvayka on 04.10.18.
 */
@Slf4j
@Component
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.ps.enabled}'=='true')")
public class PsTransportContext extends TransportContext {


    private final AtomicInteger connectionsCounter = new AtomicInteger();
//    public static Map<String, ChannelHandlerContext> deviceRelAddress = new HashMap<>();
    public static Map<String, ChannelHandlerContext> clientConnections = new HashMap<>();
//    public static Table<String, String, ChannelHandlerContext> clientConnections = HashBasedTable.create();

    @Override
    @PostConstruct
    public void init() {
        super.init();
        transportService.createGaugeStats("平升设备连接数：", connectionsCounter);
    }

    /**
     * 注册连接
     * @param channelHandlerContext
     */
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) {
        connectionsCounter.incrementAndGet();
        clientConnections.put(channelHandlerContext.channel().remoteAddress().toString(), channelHandlerContext);
    }

    /**
     * 更新设备与网络地址的关系
     * @param deviceUniqueFlag
     * @param channelHandlerContext
     */
    public void channelRegistered(String deviceUniqueFlag, ChannelHandlerContext channelHandlerContext) {
        connectionsCounter.incrementAndGet();
        clientConnections.put(deviceUniqueFlag, channelHandlerContext);
    }

    /**
     * 注销连接
     * @param channelHandlerContext
     */
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) {
        connectionsCounter.decrementAndGet();
        clientConnections.values().remove(channelHandlerContext.channel().remoteAddress().toString());
    }

    public boolean checkAddress(InetSocketAddress address) {
        return rateLimitService.checkAddress(address);
    }

    public void onAuthSuccess(InetSocketAddress address) {
        rateLimitService.onAuthSuccess(address);
    }

    public void onAuthFailure(InetSocketAddress address) {
        rateLimitService.onAuthFailure(address);
    }

}
