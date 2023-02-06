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
//package org.thingsboard.server.transport.ps.limits;
//
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
//import lombok.extern.slf4j.Slf4j;
//import org.thingsboard.server.transport.ps.PsTransportContext;
//
//import java.net.InetSocketAddress;
//
//@Slf4j
//public class IpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
//
//    private PsTransportContext context;
//
//    public IpFilter(PsTransportContext context) {
//        this.context = context;
//    }
//
//    @Override
//    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
//        log.trace("[{}] Received msg: {}", ctx.channel().id(), remoteAddress);
//        if(context.checkAddress(remoteAddress)){
//            log.trace("[{}] Setting address: {}", ctx.channel().id(), remoteAddress);
//            ctx.channel().attr(MqttTransportService.ADDRESS).set(remoteAddress);
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
