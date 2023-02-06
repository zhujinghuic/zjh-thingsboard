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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import org.thingsboard.server.transport.ps.read.PsTransportReadHandler;

import java.io.IOException;

/**
 * @author Andrew Shvayka
 */
public class PsTransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final PsTransportContext context;

    public PsTransportServerInitializer(PsTransportContext context, boolean sslEnabled) {
        this.context = context;
    }

    @Override
    public void initChannel(SocketChannel ch) throws InterruptedException, IOException {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("proxy", new HAProxyMessageDecoder());
//        if (sslEnabled && context.getSslHandlerProvider() != null) {
//            sslHandler = context.getSslHandlerProvider().getSslHandler();
//            pipeline.addLast(sslHandler);
//        }
//        pipeline.addLast("decoder", new MqttDecoder(context.getMaxPayloadSize()));
//        pipeline.addLast("encoder", MqttEncoder.INSTANCE);

        PsTransportReadHandler readHandler = new PsTransportReadHandler(context);
//        PsTransportWriteHandler writeHandler = new PsTransportWriteHandler(context);

        pipeline.addLast(readHandler);
//        pipeline.addFirst(writeHandler);
        ch.closeFuture().addListener(readHandler);
//        ch.closeFuture().addListener(writeHandler);

    }

}
