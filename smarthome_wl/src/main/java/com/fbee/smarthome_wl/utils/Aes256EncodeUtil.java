package com.fbee.smarthome_wl.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/6 14:04
 */
public class Aes256EncodeUtil {

    public static void main(String[] args) {

//        String content = "exfk";
//        String password = MD5Tools.MD5("112e5291");


//        System.out.println(MD5Tools.MD5("112e5291"));
//
//        byte[] encryptResult =encrypt(content, password);
//        System.out.println("密文：" + bytesToHexString(encryptResult));
//
//        String decryptResult = decrypt(encryptResult, password);
//        System.out.println("解密：" + decryptResult);
//        try{
//
//            byte[] bytes = encrypt(content.getBytes(), password.getBytes());
//            System.out.println("密文：" + parseByte2HexStr(bytes));
//
//        }catch (Exception e ){
//
//        }

        String str ="123456";
        String shastr = SHAEncrypt(str);
        String strs = shastr.substring(shastr.length() - 24);
        System.out.println(strs);

        Date dt = new Date();
        long second = dt.getTime()/1000;
        String secondStr = String.valueOf(second);

        String subsecond = secondStr.substring(0, secondStr.length() - 2);
        System.out.println(subsecond);
        System.out.println(SHAEncrypt(strs+subsecond));



    }

    public static byte[] encrypt(String content, String password) {
        try {
            //"AES"：请求的密钥算法的标准名称
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //256：密钥生成参数；securerandom：密钥生成器的随机源
            SecureRandom securerandom = new SecureRandom(tohash256Deal(password));
            kgen.init(256, securerandom);
            //生成秘密（对称）密钥
            SecretKey secretKey = kgen.generateKey();
            //返回基本编码格式的密钥
            byte[] enCodeFormat = secretKey.getEncoded();
            //根据给定的字节数组构造一个密钥。enCodeFormat：密钥内容；"AES"：与给定的密钥内容相关联的密钥算法的名称
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            //将提供程序添加到下一个可用位置
            Security.addProvider(new BouncyCastleProvider());
            //创建一个实现指定转换的 Cipher对象，该转换由指定的提供程序提供。
            //"AES/ECB/PKCS7Padding"：转换的名称；"BC"：提供程序的名称
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] byteContent = content.getBytes("utf-8");
            byte[] cryptograph = cipher.doFinal(byteContent);
            return cryptograph;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] cryptograph, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom securerandom = new SecureRandom(tohash256Deal(password));
            kgen.init(256, securerandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] content = cipher.doFinal(cryptograph);
            return new String(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }


    private static byte[] tohash256Deal(String datastr) {
        try {
            MessageDigest digester=MessageDigest.getInstance("SHA-256");
            digester.update(datastr.getBytes());
            byte[] hex=digester.digest();
            return hex;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * CRC
     * @param content
     * @return
     */
    public static String encodeCRC32(String content){
        CRC32 crc = new CRC32();
        crc.update(content.getBytes());
        String hex = Long.toHexString(crc.getValue());
        return String.valueOf(crc.getValue());
    }




    //-----------------------飞比提供的aes256算法------------------------------------/

    public static final String KEY_ALGORITHM="AES";
    public static final String CIPHER_ALGORITHM="AES/ECB/NoPadding";

    /**
     * 转换密钥
     * @param key 二进制密钥
     * @return Key 密钥
     * */
    public static Key toKey(byte[] key) throws Exception{
        //实例化DES密钥
        //生成密钥
        SecretKey secretKey=new SecretKeySpec(key,KEY_ALGORITHM);
        return secretKey;
    }
    /**
     * 加密数据
     * @param content 待加密数据
     * @param key 密钥
     * @return String 加密后的数据
     * */
    public static byte[] encrypt(byte[] content,byte[] key) throws Exception{
        //还原密钥
        Key k=toKey(key);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher=Cipher.getInstance("AES/ECB/NoPadding", "BC");
        //初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        //执行操作
        return cipher.doFinal(content);
    }


    /**
     * SHA-256 加密
     * @param strSrc
     * @return
     */
    public static String SHAEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = parseByte2HexStr(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }




}
