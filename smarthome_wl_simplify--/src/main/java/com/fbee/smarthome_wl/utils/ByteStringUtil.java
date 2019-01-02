package com.fbee.smarthome_wl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WLPC on 2017/4/25.
 */

public class ByteStringUtil {
    /**
     * byte[]转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str
     * @return
     */
    public static byte[] str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim().getBytes();
    }

    /**
     * 字符转字节
     *
     * @param c
     * @return
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 十六进制字符串转bytes
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 获取utc时间
     *
     * @param milliseconds
     * @return
     */
    public static String parseLong2Date(long milliseconds) {
        Date d = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String t = formatter.format(d);
        return t;
    }


    public static String stripEnd(String str, String stripChars) {
        if (str == null) {
            return null;
        }
        int end = str.length();
        if (end == 0) {
            return str;
        }
        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    //得到有效字节长度
    public static int getVirtualValueLength(byte[] buf) {
        int i = 0;
        for (; i < buf.length; i++) {
            if (buf[i] == (byte) 0) {
                break;
            }
        }
        return i;
    }

    public static String myStringToString(String a){
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i <a.length() ; i++) {
            String b="0"+a.charAt(i);
            hexString.append(b);
        }
        return hexString.toString();
    }

    /**
     * @Title:string2HexString
     * @Description:字符串转16进制字符串
     * @param strPart
     *            字符串
     * @return 16进制字符串
     * @throws
     */
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }
}
