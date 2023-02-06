package org.thingsboard.server.controller;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.components.model.inherits.write.PhotograpphPackage;
import org.thingsboard.server.transport.ps.components.model.inherits.write.SingleWritePackage;
import org.thingsboard.server.transport.ps.util.PhotographUtil;

import java.util.Locale;

@RestController
@TbCoreComponent
@RequestMapping("/api/ps")
@Slf4j
public class PsDeviceController {

    @RequestMapping(value = "/operateGate/{deviceAccess}/{action}", method = RequestMethod.GET)
    @ResponseBody
    public void openGate(@PathVariable("deviceAccess") String deviceAccess,
                         @PathVariable("action") String action) throws ThingsboardException {
        PsTransportContext.clientConnections.entrySet().stream()
                .filter(con -> con.getKey().equals(deviceAccess))
                .findAny()
                .orElseThrow(() -> new RuntimeException("该设备连接初始化中，请稍后操作"))
                .getValue()
                .channel()
                .writeAndFlush(
                        new SingleWritePackage(deviceAccess, ACTION.valueOf(action.toUpperCase(Locale.ROOT)).getValue()).sendPackage()
                );
    }

    @RequestMapping(value = "/photograph/{deviceAccess}", method = RequestMethod.GET)
    @ResponseBody
    public void photograph(@PathVariable("deviceAccess") String deviceAccess) throws ThingsboardException {
        PsTransportContext.clientConnections.entrySet().stream()
                .filter(con -> con.getKey().equals(deviceAccess))
                .findAny()
                .orElseThrow(() -> new RuntimeException("该设备连接初始化中，请稍后操作"))
                .getValue()
                .channel()
                .writeAndFlush(new PhotograpphPackage(deviceAccess).sendPackage());
    }


    enum ACTION {
        OPEN("1388"),
        CLOSE("138A"),
        STOP("138C"),
        ;

        private String value;

        ACTION(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
