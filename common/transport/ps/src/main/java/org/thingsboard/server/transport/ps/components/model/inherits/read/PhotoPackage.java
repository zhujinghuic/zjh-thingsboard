package org.thingsboard.server.transport.ps.components.model.inherits.read;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.thingsboard.server.transport.ps.components.model.PsProtocol;
import org.thingsboard.server.transport.ps.components.model.ReadPsProtocol;
import org.thingsboard.server.transport.ps.enums.PackTypeEnum;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;

/**
 * 拍照返回帧
 */
@Getter
@Setter
@Accessors(chain = true)
public class PhotoPackage extends PsProtocol {

    private PackageTypeEnum packageType;
    private String content;

    public PhotoPackage(String msg, ProtocolTypeEnum type) {
        super(type);
        this.content = msg;
        if (msg.startsWith(SYS_IDENTIFIER)) {
            this.packageType = PackageTypeEnum.PHOTO_DATA_FIRST;
        } else {
            this.packageType = PackageTypeEnum.PHOTO_DATA_SECOND;
        }
    }

    /**
     * 数据类型
     * 由于每段上位机发送的一段报文可能会出现拆包现象，
     * 完整的一段报文应该是前面固定格式ps协议头，
     * 所以报文需要分为PHOTO_DATA_FIRST， PHOTO_DATA_SECOND来拼接组装
     */
   public enum PackageTypeEnum {
        PHOTO_INFO_HEAD,
        PHOTO_DATA,
        PHOTO_DATA_FIRST,
        PHOTO_DATA_SECOND;
    }

}
