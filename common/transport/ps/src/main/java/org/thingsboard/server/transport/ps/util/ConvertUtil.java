package org.thingsboard.server.transport.ps.util;

public class ConvertUtil {
    public static double getDecimalVal(Integer val) {
        double decimalVal = 0d;
        switch (val) {
            case 1:
                decimalVal = 0.1d;
                break;
            case 2:
                decimalVal = 0.01d;
                break;
            case 3:
                decimalVal = 0.001d;
                break;
            case 4:
                decimalVal = 0.0001d;
                break;
        }
        return decimalVal;
    }
}
