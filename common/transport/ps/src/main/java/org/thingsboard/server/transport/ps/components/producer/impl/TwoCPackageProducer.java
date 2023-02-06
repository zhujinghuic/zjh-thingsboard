package org.thingsboard.server.transport.ps.components.producer.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.common.transport.adaptor.JsonConverter;
import org.thingsboard.server.common.transport.util.CrcUtils;
import org.thingsboard.server.common.transport.util.DecimalConvertUtil;
import org.thingsboard.server.common.transport.util.StreamUtil;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.callback.PsDeviceAuthCallback;
import org.thingsboard.server.transport.ps.components.model.inherits.read.TwoCPackage;
import org.thingsboard.server.transport.ps.components.producer.PsProtocolProducer;
import org.thingsboard.server.transport.ps.dto.PsConfig;
import org.thingsboard.server.transport.ps.dto.PsParseInfo;
import org.thingsboard.server.transport.ps.dto.PsRefCoilConfig;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;
import org.thingsboard.server.transport.ps.util.ConvertUtil;

import java.util.List;


@Slf4j
public class TwoCPackageProducer extends PsProtocolProducer<TwoCPackage> {

    private PsTransportContext psTransportContext;


    public TwoCPackageProducer(String msg, ProtocolTypeEnum protocolType,
                               ChannelHandlerContext ctx, PsTransportContext psTransportContext) {
        super(msg, protocolType, ctx);
        this.psTransportContext = psTransportContext;
    }

    @Override
    public void produceProtocolObj() {
        this.t = new TwoCPackage(msg, protocolType);
    }



    /**
     * 一个寄存器占2个字节 即 00 00 2位，若一个参数使用2个寄存器，则 00 00 00 00 4个字节位
     */
    @Override
    public void convert() {
//        JSON config;
        JSONObject json;
        try {
            json = JSONUtil.parseObj(
                    StreamUtil.readInputStream(new ClassPathResource("param_config/analog.json")
                            .getStream())) ;
//            config = JSONUtil.readJSON(ResourceUtils.getFile(classPathResource.getPath()), CharsetUtil.UTF_8);
        } catch (Exception e) {
            log.error("读取不到param_config/ps.json文件，请检查文件是否存在");
            throw new RuntimeException("读取不到param_config/ps.json文件，请检查文件是否存在");
        }
        JSONArray holdingRegister = (JSONArray) json.getByPath("holdingRegister");
        List<PsConfig> psConfigs = JSONUtil.toList(holdingRegister, PsConfig.class);
        int registerAddress = 35001;
        int subStartIndex = 0;
        int subEndIndex;
        // 正文内容（用户数据）
        String content = t.getModbusPakContent().substring(8);
        // 遍历所有配置地址处理
        for (int i = 0; i < psConfigs.size(); i++) {
            // 跳过没有配置的寄存器帧 除去第一个35001的地址
            if (registerAddress != psConfigs.get(i).getAddress() && psConfigs.get(i).getAddress() != 35001) {
                // 配置表中当前的寄存器地址与上一个地址相差的寄存器数
                int intervalAddress = psConfigs.get(i).getAddress() - psConfigs.get(i - 1).getAddress() - 1;
                subStartIndex = subStartIndex + intervalAddress * 4;
                // 修正当前寄存器地址
                registerAddress = psConfigs.get(i).getAddress();
            }
            PsParseInfo psParseInfo = getPsParseInfo(psConfigs.get(i));
            subEndIndex = subStartIndex + psConfigs.get(i).getRegisterNum() * 4;
            String paramValStr = content.substring(subStartIndex, subEndIndex);
            subStartIndex = subEndIndex;
            Object paramVal = convertParamVal(paramValStr, psConfigs.get(i));
            if (psConfigs.get(i).getDecimalPlaces() != 0) {
                paramVal = NumberUtil.mul(Double.parseDouble(String.valueOf(paramVal)),
                        ConvertUtil.getDecimalVal(psConfigs.get(i).getDecimalPlaces()));
            }
            psParseInfo.setParamVal(paramVal);
            t.getParams().add(psParseInfo);
            if (!CollectionUtil.isEmpty(psConfigs.get(i).getRefs())) {
                char[] values = ((String)paramVal).toCharArray();
                for (int j = 0; j < values.length; j++) {
                    PsRefCoilConfig ref = psConfigs.get(i).getRefs().get(j);
                    PsParseInfo refInfo = new PsParseInfo();
                    refInfo.setAddress(ref.getAddress());
                    refInfo.setParam(ref.getParam());
                    refInfo.setParamVal(values[j]);
                    refInfo.setRemark(ref.getKeyVal().get(String.valueOf(values[j])));
                    t.getParams().add(refInfo);
                }
            }
            // 下个遍历的地址
            registerAddress = registerAddress + psConfigs.get(i).getRegisterNum();
        }
    }

    private Object convertParamVal(String paramValStr, PsConfig psConfig) {
        Object val = null;
        switch (psConfig.getScheme()) {
            case "无符号":
                val = HexUtil.toBigInteger(paramValStr);
                break;
            case "有符号":
//                val = DecimalConvertUtil.hexString2binaryString(paramValStr.substring(0, 2));
                break;
            case "double":
                val = Double.parseDouble(paramValStr);
                break;
            default:
                if ("按位解释".equals(psConfig.getUnit())) {
                    // 解析位数
                    int positionNum;
                    // 若按位解析的地址不到8个，则只拿1个字节解析成2进制
                    if (psConfig.getRefs().size() <= 8) {
                        positionNum = 8;
                        paramValStr = paramValStr.substring(0, 2);
                    } else {
                        // 若解析2个字节，则是16位2进制
                        positionNum = 16;
                    }
                    val = DecimalConvertUtil.hexString2binaryString(paramValStr);
                    // 不足8位则补充0
                    if (((String)val).length() < positionNum) {
                        int replenish = psConfig.getRefs().size() - ((String)val).length();
                        StringBuffer bf = new StringBuffer(((String)val));
                        for (int i = 0; i < replenish; i++) {
                            bf.append("0");
                        }
                        val = bf.toString();
                    }
                }
        }
        return val;
    }


    @Override
    public void replyPackage() {
        ByteBuf byteValue = Unpooled.buffer();
        String crc16 = CrcUtils.calcCrc16(HexUtil.decodeHex(t.getModbusPakContent().substring(0, 8)));

        String hexStrPackage = new StringBuffer()
                .append(t.getPsProtocolPrefix())
                .append(t.getModbusPakContent(), 0, 8)
                .append(crc16)
                .toString();

        hexStrPackage = hexStrPackage + CrcUtils.yihuo(hexStrPackage);
        byte[] bytes = HexUtil.decodeHex(hexStrPackage);
        byteValue.writeBytes(bytes);// 对接需要16进制
        ctx.writeAndFlush(byteValue);
        log.info("回复客户端-{}-报文-{}", ctx.channel().remoteAddress().toString(), hexStrPackage);
    }

//    public static void main(String[] args) {
//        System.out.println(CrcUtils.yihuo("123456001C80010000000000100B138123456780652C0050df1d"));
//    }

    @Override
    public void process() throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(t);
        getPsTransportContext().getTransportService().process(DeviceTransportType.PS, TransportProtos.ValidateDeviceTokenRequestMsg.newBuilder().setToken(t.getDeviceUniqueFlag()).build(),
                new PsDeviceAuthCallback(psTransportContext, sessionInfo -> {
                    TransportService transportService = psTransportContext.getTransportService();
                    transportService.process(sessionInfo, JsonConverter.convertToTelemetryProto(new JsonParser().parse(json)),
                            TransportServiceCallback.EMPTY);
                }));
    }

    private PsParseInfo getPsParseInfo(PsConfig psConfig) {
        PsParseInfo psParseInfo = new PsParseInfo();
        BeanUtil.copyProperties(psConfig, psParseInfo);
        return psParseInfo;
    }

    public PsTransportContext getPsTransportContext() {
        return psTransportContext;
    }

}
