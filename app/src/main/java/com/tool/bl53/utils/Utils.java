package com.tool.bl53.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    private static final String TAG = "Utils";
    public static final byte[] DEFAULT_KEY_1 = {0x20, 0x57, 0x2F, 0x52, 0x36, 0x4B, 0x3F, 0x47, 0x30, 0x50, 0x41, 0x58, 0x11, 0x63, 0x2D, 0x2B};

    public static String getVersionName(Context context) {
        String ret = "";
        try {
            ret = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static byte[] encryptAes128(byte[] sSrc, byte[] sKey) {
        try {
            SecretKeySpec sks = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            return cipher.doFinal(sSrc);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            return null;
        }
    }

    public static byte[] decryptAes128(byte[] sSrc, byte[] sKey) {
        try {
            SecretKeySpec sks = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            return cipher.doFinal(sSrc);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            return null;
        }
    }

    public static String debugByteData(byte[] data) {
        StringBuilder ret = new StringBuilder();
        for (byte datum : data) {
            ret.append(String.format("%02X ", datum));
        }
        return ret.toString();
    }

//    public static byte[] getKeyData(final String mac, final Device.Type deviceType) {
////        return DEFAULT_KEY_3;
//        byte[] ret = DEFAULT_KEY_1;
//        if (mac != null) {
//            if (mac.equals("9C:F6:DD:60:21:A4")) {
//                ret = Base64.decode("g1Wt61NFS5D+Rz7hr+Qr6A==", Base64.DEFAULT);
//            } else if (mac.equals("9C:F6:DD:65:53:DF")) {
//                ret = Base64.decode("f7hsEiXAqOSIpGpxa21/3A==", Base64.DEFAULT);
//            } else if (mac.equals("9C:F6:DD:61:09:F6")) {
//                ret = Base64.decode("e7lRYtZ/u/owj9NNPv0n0Q==", Base64.DEFAULT);
//            } else if (mac.equals("9C:F6:DD:61:6A:D1")) {
//                ret = Base64.decode("R25E6NH5VzeZDcq+ZWCBtA==", Base64.DEFAULT);
//            } else if (mac.equals("9C:F6:DD:62:01:0D")) {
//                ret = Base64.decode("fxtwm0y0mQR8PEhlpmjfxQ==", Base64.DEFAULT);
//            } else if (mac.equals("10:CE:A9:44:91:00")) {
//                ret = new byte[]{0x25, 0x2F, 0x54, 0x10, 0x11, 0x34, 0x58, 0x63, 0x0F, 0x0C, 0x1C, 0x17, 0x1F, 0x24, 0x08, 0x35};
//            }
//        }
//        return ret;
//    }

    public static byte[] getPassword(final String mac) {
        byte[] buff = null;
        if (mac != null) {
            if (mac.equals("9C:F6:DD:60:21:A4")) {
                buff = Base64.decode("NzI2Njk0", Base64.DEFAULT);
            } else if (mac.equals("9C:F6:DD:65:53:DF")) {
                buff = Base64.decode("Njc3OTE3", Base64.DEFAULT);
            } else if (mac.equals("9C:F6:DD:61:09:F6")) {
                buff = Base64.decode("ODAyOTYx", Base64.DEFAULT);
            } else if (mac.equals("9C:F6:DD:61:6A:D1")) {
                buff = Base64.decode("NDY5MDYw", Base64.DEFAULT);
            } else if (mac.equals("9C:F6:DD:62:01:0D")) {
                buff = Base64.decode("NDk0ODQ1", Base64.DEFAULT);
            } else if (mac.equals("10:CE:A9:44:91:00")) {
                buff = new byte[]{0x54, 0x14, 0x49, 0x35, 0x41, 0x28};
            }
        }
        if (buff == null) {
            buff = new byte[]{0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
        }
        return buff;
    }

    public static int byteArray2int(byte[] bs) {
        int i = 0;
        for (int m = 0; m < bs.length; m++) {
            i |= ((bs[m] < 0) ? (bs[m] + 256) : bs[m]) << ((bs.length - m - 1) * 8);
        }
        return i;
    }


    public static byte int2byte(int i) {
        byte b;
        if (i < 0x80) {
            b = (byte) i;
        } else {
            b = (byte) (i - 256);
        }
        return b;
    }


    public static Map<String, Integer> getBroadcastId(byte[] scanRecord) {
        int deviceType = -1;
        int deviceId = -1;
        if (scanRecord != null) {
//            Log.d(TAG, debugByteData(scanRecord));
            int pos = 0;
            for (int i = 0; i < scanRecord.length; i++) {
                int len = byteArray2int(Arrays.copyOfRange(scanRecord, pos, pos + 1));
                if (len == 0) break;
                int type = byteArray2int(Arrays.copyOfRange(scanRecord, pos + 1, pos + 2));
                if (type == 0xFF) {
                    try {
                        byte[] value = Arrays.copyOfRange(scanRecord, pos + 2, pos + 2 + len - 1);
                        deviceType = byteArray2int(Arrays.copyOfRange(value, 0, 2));
                        if (value.length >= 10) {
                            deviceId = byteArray2int(Arrays.copyOfRange(value, 8, 10));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                pos += len + 1;
            }
        }
        Map<String, Integer> ret = new HashMap<>(2);
        ret.put("deviceType", deviceType);
        ret.put("deviceId", deviceId);
        return ret;
    }
}
