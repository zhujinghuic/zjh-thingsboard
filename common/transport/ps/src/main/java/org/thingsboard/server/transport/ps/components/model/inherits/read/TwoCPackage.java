package org.thingsboard.server.transport.ps.components.model.inherits.read;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.thingsboard.server.transport.ps.components.model.ReadPsProtocol;
import org.thingsboard.server.transport.ps.dto.PsPakInfo;
import org.thingsboard.server.transport.ps.enums.PackTypeEnum;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class TwoCPackage extends ReadPsProtocol {

    private String modbusPakContent;
    private List<PsPakInfo> params;

    {
        this.params = new ArrayList<>();
    }


    public TwoCPackage(String msg, ProtocolTypeEnum type) {
        super(msg, type);
        this.modbusPakContent = msg.substring(42, msg.length() - 6);
    }

    @Override
    public void setSendPsProtocolPrefix() {
        this.psProtocolPrefix = new StringBuffer()
                .append(SYS_IDENTIFIER)
                .append("001C") // 帧长度为17
                .append(PACK_NUM)
                .append(PackTypeEnum.digital.getCode())
                .append(DOT_LEN)
                .append(serverUniqueFlag)
                .append(DOT_LEN)
                .append(deviceUniqueFlag)
                .toString();
    }


//    public static void main(String[] args) {
//        System.out.println(HexUtil.toBigInteger("E9D90000"));
//    }

}
