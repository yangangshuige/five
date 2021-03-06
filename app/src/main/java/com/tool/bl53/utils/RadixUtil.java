package com.tool.bl53.utils;

public class RadixUtil {
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=(byte)Integer.parseInt(inHex.substring(i,i+2),16);
            j++;
        }
        return result;
    }

    public static  String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);

            if(hex.length() < 2){
                sb.append(0);
            }

            sb.append(hex);
        }

        return sb.toString().toUpperCase();
    }

    public static  String byteToHex(Integer data) {
        StringBuffer sb = new StringBuffer();


            String hex = Integer.toHexString(data & 0xFF);

            if(hex.length() < 2){
                sb.append(0);
            }

            sb.append(hex);


        return sb.toString().toUpperCase();
    }
}
