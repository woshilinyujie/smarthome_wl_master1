package com.example.wl.WangLiPro_v1.bean;

import com.example.wl.WangLiPro_v1.utils.MD5Tools;

import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 登录返回
 *
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 10:04
 */
public class LoginResult implements Serializable {

    private HeaderBean header;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public static class HeaderBean implements Serializable {
        /**
         * api_version : 1.0
         * message_type : MSG_USER_CONFIG_QUERY_RSP
         * seq_id : 1
         * http_code : 200
         * return_string : Success OK
         */

        private String api_version;
        private String message_type;
        private String seq_id;
        private String http_code;
        private String return_string;

        public String getApi_version() {
            return api_version;
        }

        public void setApi_version(String api_version) {
            this.api_version = api_version;
        }

        public String getMessage_type() {
            return message_type;
        }

        public void setMessage_type(String message_type) {
            this.message_type = message_type;
        }

        public String getSeq_id() {
            return seq_id;
        }

        public void setSeq_id(String seq_id) {
            this.seq_id = seq_id;
        }

        public String getHttp_code() {
            return http_code;
        }

        public void setHttp_code(String http_code) {
            this.http_code = http_code;
        }

        public String getReturn_string() {
            return return_string;
        }

        public void setReturn_string(String return_string) {
            this.return_string = return_string;
        }
    }

    /**
     * body : {"secret_key":"xxxxxxxxx","user_alias":"xxxxxxx","father_username":"xxxxxxxxx","gateway_list":[{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"}],"child_user_list":[{"username":"xxxxxxx","user_alias":"老婆"},{"username":"xxxxxxx","user_alias":"女儿"}]}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }


    public static class BodyBean implements Serializable {
        /**
         * secret_key : xxxxxxxxx
         * user_alias : xxxxxxx
         * father_username : xxxxxxxxx
         * gateway_list : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"}]
         * child_user_list : [{"username":"xxxxxxx","user_alias":"老婆"},{"username":"xxxxxxx","user_alias":"女儿"}]
         */

        private String secret_key;
        private String user_alias;
        private String father_username;
        private List<GatewayListBean> gateway_list;
        private List<ChildUserListBean> child_user_list;
        private List<String> slideshow;

        public List<String> getSlideshow() {
            return slideshow;
        }

        public void setSlideshow(List<String> slideshow) {
            this.slideshow = slideshow;
        }

        public String getSecret_key() {
            return secret_key;
        }

        public void setSecret_key(String secret_key) {
            this.secret_key = secret_key;
        }

        public String getUser_alias() {
            return user_alias;
        }

        public void setUser_alias(String user_alias) {
            this.user_alias = user_alias;
        }

        public String getFather_username() {
            return father_username;
        }

        public void setFather_username(String father_username) {
            this.father_username = father_username;
        }

        public List<GatewayListBean> getGateway_list() {
            if (gateway_list == null) {
                gateway_list = new ArrayList<>();
            }
            return gateway_list;
        }

        public void setGateway_list(List<GatewayListBean> gateway_list) {
            if (this.gateway_list == null) {
                this.gateway_list = new ArrayList<>();
            }
            this.gateway_list = gateway_list;
        }

        public List<ChildUserListBean> getChild_user_list() {
            if (child_user_list == null) {
                child_user_list = new ArrayList<ChildUserListBean>();
            }
            return child_user_list;
        }

        public void setChild_user_list(List<ChildUserListBean> child_user_list) {
            this.child_user_list = child_user_list;
        }

        public static class GatewayListBean implements Serializable {
            /**
             * vendor_name : feibee
             * uuid : xxxxxxxxxx
             * username : xxxxxxxxx
             * password : xxxxxxxxx
             * authorization : admin
             * note : 网网的网关
             * version : xxxxx
             */

            private String vendor_name;
            private String uuid;
            private String username;
            private String password;
            private String authorization;
            private String note;
            private String version;

            public String getVendor_name() {
                return vendor_name;
            }

            public void setVendor_name(String vendor_name) {
                this.vendor_name = vendor_name;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                String psw = password;
                try {
                    psw = decrypt(password, uuid);
                } catch (Exception e) {
                }
                return psw;
            }

            /**
             * 解密数据
             */
            public static String decrypt(String data, String snid) {
                try {
                    String snidmd5 = MD5Tools.MD5(snid);
                    return new String(decrypt(hexStringToBytes(data), snidmd5.getBytes()));
                } catch (Exception e) {
                    return "";
                }
            }

            /**
             * 密钥算法
             * java6支持56位密钥，bouncycastle支持64位
             */
            public static final String KEY_ALGORITHM = "AES";

            /**
             * 加密/解密算法/工作模式/填充方式
             * <p>
             * JAVA6 支持PKCS5PADDING填充方式
             * Bouncy castle支持PKCS7Padding填充方式
             */
            public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

            /**
             * 解密数据
             *
             * @param data 待解密数据
             * @param key  密钥
             * @return byte[] 解密后的数据
             */
            private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
                //欢迎密钥
                Key k = toKey(key);
                /**
                 * 实例化
                 * 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
                 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
                 */
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                //初始化，设置为解密模式
                cipher.init(Cipher.DECRYPT_MODE, k);
                //执行操作
                return cipher.doFinal(data);
            }

            /**
             * 转换密钥
             *
             * @param key 二进制密钥
             * @return Key 密钥
             */
            public static Key toKey(byte[] key) throws Exception {
                //实例化DES密钥
                //生成密钥
                SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
                return secretKey;
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
             * 字符转字节
             *
             * @param c
             * @return
             */
            public static byte charToByte(char c) {
                return (byte) "0123456789ABCDEF".indexOf(c);
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getAuthorization() {
                return authorization;
            }

            public void setAuthorization(String authorization) {
                this.authorization = authorization;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

        public static class ChildUserListBean implements Serializable {
            /**
             * username : xxxxxxx
             * user_alias : 老婆
             */

            private String username;
            private String user_alias;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUser_alias() {
                return user_alias;
            }

            public void setUser_alias(String user_alias) {
                this.user_alias = user_alias;
            }
        }
    }


}
