package org.thingsboard.server.transport.ps.components.producer.impl;

import cn.hutool.core.util.HexUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.thingsboard.server.transport.ps.PsTransportContext;
import org.thingsboard.server.transport.ps.components.dto.PhotoContent;
import org.thingsboard.server.transport.ps.components.model.inherits.read.PhotoPackage;
import org.thingsboard.server.transport.ps.components.producer.PsProtocolProducer;
import org.thingsboard.server.transport.ps.enums.ProtocolTypeEnum;
import org.thingsboard.server.transport.ps.util.PhotographUtil;
import org.thingsboard.server.transport.ps.util.TestHexToJpg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
public class PhotoProducer extends PsProtocolProducer<PhotoPackage> {

    private RedisTemplate redisTemplate;
//    private final int PHOTO_DATA_SAVE_TIME = 60;
    private String redisKey;
    private String tempKey;
    private String headKey;

    public PhotoProducer(String msg, ProtocolTypeEnum protocolType, ChannelHandlerContext ctx) {
        super(msg, protocolType);
        this.redisTemplate = SpringUtil.getBean("redisTemplate");
        this.ctx = ctx;
    }

    @Override
    public void produceProtocolObj() {
        this.t = new PhotoPackage(msg, protocolType);
        fillAttribute();
    }

    /**
     * 设置属性
     */
    private void fillAttribute() {
        t.setDeviceUniqueFlag(
                PsTransportContext.clientConnections.entrySet().stream()
                        .filter(con -> con.getValue().channel().remoteAddress().toString().equals(ctx.channel().remoteAddress().toString()))
                        .findAny().get().getKey()
        );
        this.redisKey = PhotographUtil.getPhotoDataKey(t.getDeviceUniqueFlag());
        this.tempKey = PhotographUtil.getPhotoTempKey(t.getDeviceUniqueFlag());
        this.headKey = PhotographUtil.getPhotoHeadKey(t.getDeviceUniqueFlag());
    }


    @Override
    public void convert() {
        synchronized (PhotoProducer.class) {
            try {
                String content = "";
                switch (t.getPackageType()) {
                    case PHOTO_DATA_FIRST:
                        redisTemplate.opsForSet().add(redisKey, t.getContent());
                        redisTemplate.opsForValue().set(tempKey, t.getContent());
                        content = t.getContent();
                        break;
                    case PHOTO_DATA_SECOND:
                        String prePakContent = (String) redisTemplate.opsForValue().get(tempKey);
                        content = new StringBuffer(prePakContent).append(t.getContent()).toString();
                        redisTemplate.opsForValue().set(tempKey, content);
                        if (content.length() > 74) {
                            redisTemplate.opsForSet().remove(redisKey, prePakContent);
                            redisTemplate.opsForSet().add(redisKey, content);
                        }
                        break;
                }
                long size = redisTemplate.opsForSet().size(redisKey);
                if (size == 1 && content.length() == 74) {
                    redisTemplate.opsForSet().pop(redisKey);
                    redisTemplate.opsForValue().set(headKey, content);
                }
            } catch (Exception e) {
                log.error("接收照片数据出错，设备标识-{}，错误原因-", t.getDeviceUniqueFlag(), e);
                PhotographUtil.clearRedisPhotoData(t.getDeviceUniqueFlag());
            }
        }
    }


    @Override
    public void process() throws JsonProcessingException {
        try {
            List<PhotoContent> sortList = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            Set<String> photoData = redisTemplate.opsForSet().members(redisKey);
            String headContent = (String) redisTemplate.opsForValue().get(headKey);
//        int pakSize = HexUtil.toBigInteger(headContent.substring(56, 60)).intValue();
            int pakCount = HexUtil.toBigInteger(headContent.substring(64, 66)).intValue();
            if (pakCount == photoData.size()) {
                photoData.forEach(data -> sortList.add(
                        PhotoContent.builder()
                                .num(HexUtil.toBigInteger(data.substring(56, 58)).intValue())
                                .content(data.substring(60, data.length() - 6))
                                .build()));
                sortList.sort(Comparator.comparing(PhotoContent::getNum));
                sortList.forEach(data -> buffer.append(data.getContent()));
                TestHexToJpg.hexToImage(buffer.toString());
                //完成一组照片上传，清空redis数据
                PhotographUtil.clearRedisPhotoData(t.getDeviceUniqueFlag());
            }
        } catch (Exception e) {
            log.error("接收照片数据出错，设备标识-{}，错误原因-", t.getDeviceUniqueFlag(), e);
        } finally {
//            PhotographUtil.clearRedisPhotoData(t.getDeviceUniqueFlag());
        }
    }

}
