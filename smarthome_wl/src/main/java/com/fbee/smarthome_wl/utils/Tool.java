package com.fbee.smarthome_wl.utils;

/**
 * Created by WLPC on 2017/3/31.
 */

public class Tool {
    public static String BytesToHexString(byte[] src) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        if ((src == null) || (src.length <= 0)) {
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

    public static byte[] HexStringToBytes(String hexString) throws Exception {
        if ((hexString == null) || (hexString.equals(""))) {
            return null;
        }
        hexString = hexString.toUpperCase(java.util.Locale.getDefault());
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = ((byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)])));
        }
        return d;
    }

    public static String HexStringToBytes1(String hexString) throws Exception {
        if ((hexString == null) || (hexString.equals(""))) {
            return null;
        }
        hexString = hexString.toUpperCase(java.util.Locale.getDefault());
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = ((byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)])));
            str.append(Integer.toHexString(d[i] & 0xff));
            str.append(" ");
        }
        return str.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     *
     * @param num
     * @return  高位在前，低位在后
     */
    public static byte[] IntToBytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >> 24) & 0xff);
        result[1] = (byte) ((num >> 16) & 0xff);
        result[2] = (byte) ((num >> 8) & 0xff);
        result[3] = (byte) ((num >> 0) & 0xff);
        return result;
    }

    /**
     *
     * @param bytes
     * @return 高位在前，低位在后
     */
    public static int BytesToInt(byte[] bytes) {
        int result = 0;
        if(bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }
        return result;
    }
    /**16进制转换为2进制*/
//    public static String hexString2binaryString(String hexString)
//    {
//        if (hexString == null || hexString.length() % 2 != 0)
//            return null;
//        String bString = "", tmp;
//        for (int i = 0; i < hexString.length(); i++)
//        {
//            tmp = "0000"
//                    + Integer.toBinaryString(Integer.parseInt(hexString
//                    .substring(i, i + 1), 16));
//            bString += tmp.substring(tmp.length() - 4);
//        }
//        return bString;
//    }
    /**
     *
     * @param bytes
     * @return 高位在后，低位在前
     */
    public static int BytesToUSInt(byte c1,byte c2) {
        int result = 0;
        int c = (c2 & 0xff) << 8;
        int d = (c1 & 0xff);
        result =  c | d;

        return result;
    }
}
