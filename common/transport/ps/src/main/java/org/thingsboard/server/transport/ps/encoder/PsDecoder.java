package org.thingsboard.server.transport.ps.encoder;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.components.action.PsPackageProcess;
import org.thingsboard.server.transport.ps.components.producer.impl.HeartbeatReqProducer;
import org.thingsboard.server.transport.ps.components.producer.impl.NoActionProducer;
import org.thingsboard.server.transport.ps.components.producer.impl.PhotoProducer;
import org.thingsboard.server.transport.ps.components.producer.impl.TwoCPackageProducer;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

@Slf4j
public class PsDecoder {
    private PsTransportContext psTransportContext;
    private ChannelHandlerContext ctx;

    public PsDecoder(PsTransportContext psTransportContext, ChannelHandlerContext ctx) {
        this.psTransportContext = psTransportContext;
        this.ctx = ctx;
    }

    /**
     *         // 客户端的注册报文或心跳报文
     *         //12 34 56 00 17 80 06 0B 13 81 23 45 67 80 0B 00 00 00 00 00 10 33 D1
     *         // 12 34 56 00 17 80 06 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 55 B7
     *
     *         // 客户端主动上报报文
     *     // 12 34 56 00 6C 80 01 0B 13 81 23 45 67 80 0B 00 00 00 00 00 10 65 2C 00 50 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 15 04 EB D9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 52 8A 7C
     *     // 12 34 56 00 1C 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 2C 00 50 DF 1D 35
     *
     *     // 12 34 56 00 50 80 01 0B 13 81 23 45 67 80 0B 00 00 00 00 00 10 65 2C 00 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 16 04 DE 01 00 00 00 00 00 00 00 3F 83 AE
     *     // 12 34 56 00 1C 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 2C 00 34 DE F6 BB
     *         // 读模拟量
     *         // 12 34 56 00 1E 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 04 13 9E 00 01 5C 84 D9
     *         // 12 34 56 00 1D 80 01 0B 13 81 23 45 67 80 0B 00 00 00 00 00 10 65 04 02 01 00 C9 68 2C
     *
     *         // 控制闸门
     *         // 开闸门
     *         // 12 34 56 00 1E 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 05 13 88 FF 00 00 B0 58
     *         // 关闸门
     *         // 12 34 56 00 1E 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 05 13 8A FF 00 A1 70 3B
     *         // 停闸门
     *         // 12 34 56 00 1E 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 65 05 13 8C FF 00 41 71 DC
     *
     *         // 拍照
     *         // 12 34 56 00 1E 80 01 0B 00 00 00 00 00 10 0B 13 81 23 45 67 80 90 EB 01 30 00 00 C1 C2 A5
     *
     *         // 拍照返回照片文件信息
     *         // 12 34 56 00 25 80 01 0B 13 81 23 45 67 80 0B 00 00 00 00 00 10 90 EB 01 30 07 00 00 30 58 00 00 2D 00 50 8C 03
     * @param msg
     * @return
     */
    public PsPackageProcess decoder(String msg) throws InterruptedException {
        PsPackageProcess action = null;
        if (msg.length() == 46) {
            if (ProtocolTypeEnum.REGISTER.getCode().equals(msg.substring(msg.length() - 4, msg.length() - 2))) {
                action = new PsPackageProcess(new HeartbeatReqProducer(msg, ProtocolTypeEnum.REGISTER, ctx, psTransportContext));
            }
        } else if (msg.length() > 46) {
            String fun = msg.substring(44, 46);
            ProtocolTypeEnum type = ProtocolTypeEnum.get(fun);
            switch (type) {
                case CONTENT:
                    action = new PsPackageProcess(new TwoCPackageProducer(msg, type, ctx, psTransportContext));
                    break;
                case WRITE:
                    action = new PsPackageProcess(new NoActionProducer(msg, type));
                    break;
                default:
//                    Thread.sleep(1000L);
                    action = new PsPackageProcess(new PhotoProducer(msg, type, ctx));
            }
        } else {
            action = new PsPackageProcess(new PhotoProducer(msg,  ProtocolTypeEnum.PHOTOGRAPH, ctx));
        }
        return action;
    }

}
