package com.tool.bl53.biz;

import java.util.Locale;

public class Test {
    public static String test() {
        byte[] data = new byte[]{
                0x00, (byte) 0x80, 0x08, (byte) 0xB1,
                (byte) 0xA1, (byte) 0x92,
                (byte) 0x83, 0x01, 0x00, 0x21, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        String strVersion = String.format(Locale.getDefault(), "%d.%d", data[8] >= 0 ? data[8] : (data[8] + 256), data[9] >= 0 ? data[9] : (data[9] + 256));
        float fVersion = Float.parseFloat(strVersion);
        int hMicroVer = (data[11] < 0) ? (data[11] + 256) : data[11];
        int lMicroVer = (data[12] < 0) ? (data[12] + 256) : data[12];
        return "V" + strVersion + "." + (hMicroVer * 256 + lMicroVer);
    }

}
