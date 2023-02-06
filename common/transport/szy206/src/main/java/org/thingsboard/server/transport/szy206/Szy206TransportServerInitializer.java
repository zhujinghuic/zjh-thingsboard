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
package org.thingsboard.server.transport.szy206;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.ssl.SslHandler;
import org.thingsboard.server.transport.szy206.limits.IpFilter;

/**
 * @author Andrew Shvayka
 */
public class Szy206TransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Szy206TransportContext context;
    private final boolean sslEnabled;

    public Szy206TransportServerInitializer(Szy206TransportContext context, boolean sslEnabled) {
        this.context = context;
        this.sslEnabled = sslEnabled;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        SslHandler sslHandler = null;
        if (context.isProxyEnabled()) {
            pipeline.addLast("proxy", new HAProxyMessageDecoder());
            pipeline.addLast("ipFilter", new IpFilter(context));
        } else {
            pipeline.addLast("ipFilter", new IpFilter(context));
        }

        Szy206TransportHandler handler = new Szy206TransportHandler(context, sslHandler);

        pipeline.addLast(handler);
        ch.closeFuture().addListener(handler);
    }

}
