package org.thingsboard.server.transport.szy206.encoder;

import io.netty.buffer.ByteBuf;

public class CrcUtils {
    public static String CRC_16(byte[] bytes) {
        int i, j, lsb;
        int h = 0xffff;
        for (i = 0; i < bytes.length; i++) {
            h ^= bytes[i];
            for (j = 0; j < 8; j++) {
                lsb = h & 0x0001; // 取 CRC 的移出位
                h >>= 1;
                if (lsb == 1) {
                    h ^= 0x8408;
                }
            }
        }
        h ^= 0xffff;
        return Integer.toHexString(h).toUpperCase();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static String bytesToHexString(ByteBuf buffer) {
        final int length = buffer.readableBytes();
        StringBuffer sb = new StringBuffer(length);
        String sTmp;

        for (int i = 0; i < length; i++) {
            byte b = buffer.readByte();
            sTmp = Integer.toHexString(0xFF & b);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }
}
