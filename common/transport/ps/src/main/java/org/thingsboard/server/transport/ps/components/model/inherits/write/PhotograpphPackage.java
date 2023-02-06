package org.thingsboard.server.transport.ps.components.model.inherits.write;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import org.thingsboard.server.common.transport.util.CrcUtils;
import org.thingsboard.server.transport.ps.components.model.PsProtocol;
import org.thingsboard.server.transport.ps.enums.PackTypeEnum;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 写帧-单个寄存器
 */
@Data
public class PhotograpphPackage extends PsProtocol {

    public PhotograpphPackage(String deviceUniqueFlag) {
        super(ProtocolTypeEnum.WRITE);
        this.deviceUniqueFlag = deviceUniqueFlag;
        this.setSendPsProtocolPrefix();
    }

    @Override
    public void setSendPsProtocolPrefix() {
        this.psProtocolPrefix = new StringBuffer()
                .append(SYS_IDENTIFIER)
                .append("001E") // 帧长度为1E
                .append(PACK_NUM)
                .append(PackTypeEnum.digital.getCode())
                .append(DOT_LEN)
                .append("000000000010")
                .append(DOT_LEN)
                .append(deviceUniqueFlag)
                .toString();
    }

    public ByteBuf sendPackage() {
        String photoProtocol = "90EB01300000C1C2";

        String pak = new StringBuffer()
                .append(this.psProtocolPrefix)
                .append(photoProtocol)
                .toString();

        pak = new StringBuffer()
                .append(pak)
                .append(CrcUtils.yihuo(pak))
                .toString();
        return Unpooled.copiedBuffer(HexUtil.decodeHex(pak));
    }
}
