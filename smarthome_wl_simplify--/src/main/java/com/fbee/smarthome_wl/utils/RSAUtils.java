package com.fbee.smarthome_wl.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


/**
 * @class name：com.fbee.smarthome_wl.utils
 * @anthor create by Zhaoli.Wang
 * @time 2017/5/11 15:46
 */
public class RSAUtils {


    /**
     *加密的方法
     */
    public  static String encrypt(String source,String key) {
        String epStr="";

        try{
            LogUtil.e("PUCLIC_KEY:index",String.valueOf(key.indexOf("\n-----END PUBLIC KEY-----")));
            String PUCLIC_KEY =key.substring(27,key.indexOf("\n-----END PUBLIC KEY-----"));
            LogUtil.e("PUCLIC_KEY:",PUCLIC_KEY);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(PUCLIC_KEY));
            byte[] sbt = source.getBytes("UTF-8");
            byte[] output = cipher.doFinal(sbt);
            epStr =  Base64Util.encode(output);
            LogUtil.e("PUCLIC_KEY+encrypt:",epStr);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return epStr;
    }



    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static Key loadPublicKey(String publicKeyStr) throws Exception
    {
        try
        {
            byte[] buffer = Base64Util.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e)
        {
            throw new Exception("公钥非法");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥数据为空");
        }
    }

}
