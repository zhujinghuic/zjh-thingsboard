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

import cn.hutool.extra.spring.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.TbTransportService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Andrew Shvayka
 */
@Service("PsTransportService")
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.ps.enabled}'=='true')")
@Slf4j
public class PsTransportService implements TbTransportService {

    @Value("${transport.ps.bind_address}")
    private String host;
    @Value("${transport.ps.bind_port}")
    private Integer port;

    @Value("${transport.ps.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;
    @Value("${transport.ps.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;
    @Value("${transport.ps.netty.so_keep_alive}")
    private boolean keepAlive;

    @Autowired
    private PsTransportContext context;

    @Bean
    public SpringUtil getSpringUtil() {
        return new SpringUtil();
    }

    private Channel serverChannel;
    private Channel sslServerChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @PostConstruct
    public void init() throws Exception {

        log.info("Starting PS transport...");
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new PsTransportServerInitializer(context, false))
                .childOption(ChannelOption.SO_KEEPALIVE, keepAlive);

        serverChannel = b.bind(host, port).sync().channel();
        log.info("PS transport started!");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping PS transport!");
        try {
            serverChannel.close().sync();
//            if (sslEnabled) {
//                sslServerChannel.close().sync();
//            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("PS transport stopped!");
    }

    @Override
    public String getName() {
        return DataConstants.PS_TRANSPORT_NAME;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }


}
