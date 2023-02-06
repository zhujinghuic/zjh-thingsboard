package org.thingsboard.server.common.transport.util;

import java.math.BigInteger;

public class DecimalConvertUtil {

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

    /**
     * 将16进制转换为二进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        //16进制转10进制
        BigInteger sint = new BigInteger(hexString, 16);
        //10进制转2进制
        String result = sint.toString(2);
        //字符串反转
        return new StringBuilder(result).reverse().toString();
    }

//    public static void main(String[] args) {
//        System.out.println(hexString2binaryString("D900"));
//    }
}
