package com.fbee.smarthome_wl.bean;

/**
 * 6.3 透传-设备用户配置
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/26 17:17
 */
public class UserCofigSetting {

    /**
     * timestamp : 1499681215
     * cmd : DEVICE_USER_CONFIG
     * unlock_adm_pwd : xxxxx,xxxxxx
     * be_operated : {"mode":"del","object":"001","object_level":"adm","unlock_mode":"pwd,fp,card"}
     */

    private String timestamp;
    private String cmd;
    private String unlock_adm_pwd;
    private BeOperatedBean be_operated;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUnlock_adm_pwd() {
        return unlock_adm_pwd;
    }

    public void setUnlock_adm_pwd(String unlock_adm_pwd) {
        this.unlock_adm_pwd = unlock_adm_pwd;
    }

    public BeOperatedBean getBe_operated() {
        return be_operated;
    }

    public void setBe_operated(BeOperatedBean be_operated) {
        this.be_operated = be_operated;
    }

    public static class BeOperatedBean {
        /**
         * mode : del
         * object : 001
         * object_level : adm
         * unlock_mode : pwd,fp,card
         */

        private String mode;
        private String object;
        private String object_level;
        private String unlock_mode;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public String getObject_level() {
            return object_level;
        }

        public void setObject_level(String object_level) {
            this.object_level = object_level;
        }

        public String getUnlock_mode() {
            return unlock_mode;
        }

        public void setUnlock_mode(String unlock_mode) {
            this.unlock_mode = unlock_mode;
        }

        @Override
        public String toString() {
            return "BeOperatedBean{" +
                    "mode='" + mode + '\'' +
                    ", object='" + object + '\'' +
                    ", object_level='" + object_level + '\'' +
                    ", unlock_mode='" + unlock_mode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserCofigSetting{" +
                "timestamp='" + timestamp + '\'' +
                ", cmd='" + cmd + '\'' +
                ", unlock_adm_pwd='" + unlock_adm_pwd + '\'' +
                ", be_operated=" + be_operated.toString() +
                '}';
    }
}
