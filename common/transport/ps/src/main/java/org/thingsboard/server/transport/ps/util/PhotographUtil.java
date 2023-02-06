package org.thingsboard.server.transport.ps.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.core.RedisTemplate;

import static org.thingsboard.server.transport.ps.components.model.inherits.read.PhotoPackage.PackageTypeEnum.PHOTO_DATA;
import static org.thingsboard.server.transport.ps.components.model.inherits.read.PhotoPackage.PackageTypeEnum.PHOTO_INFO_HEAD;

/**
 * 拍照功能的公用方法类
 */
public class PhotographUtil {

    static private RedisTemplate redisTemplate;

    static {
        redisTemplate = SpringUtil.getBean("redisTemplate");
    }

    /**
     * 获取存放照片报文的key
     * @param deviceAccess
     * @return
     */
    public static String getPhotoDataKey(String deviceAccess) {
        return new StringBuffer(PHOTO_DATA.name())
                .append("@")
                .append(deviceAccess)
                .toString();
    }

    /**
     * 获取存放照片文件信息的key
     * @param deviceAccess
     * @return
     */
    public static String getPhotoHeadKey(String deviceAccess) {
        return new StringBuffer(PHOTO_INFO_HEAD.name())
                .append("@")
                .append(deviceAccess)
                .toString();
    }

    /**
     * 获取存放照片最新一条的key
     * @param deviceAccess
     * @return
     */
    public static String getPhotoTempKey(String deviceAccess) {
        return new StringBuffer(getPhotoDataKey(deviceAccess)).append("_temp").toString();
    }

    /**
     * 清空照片数据
     * @param deviceAccess
     */
    public static void clearRedisPhotoData(String deviceAccess) {
        redisTemplate.delete(getPhotoHeadKey(deviceAccess));
        redisTemplate.delete(getPhotoDataKey(deviceAccess));
        redisTemplate.delete(getPhotoTempKey(deviceAccess));
    }
}
