package com.fbee.smarthome_wl.constant;

/**
 * 请求返回码
 * Created by ZhaoLi.Wang on 2017/1/3 11:46
 */
public class StatusCode {

    /**
     * 数据需要重新加载的时候
     */
    public static final int REFRESH_DATA = 200010;

    /**
     * 手机号错误或搜索不到用户或者用户已注册但未认证
     */
    public static final int SEARCH_STAFF_ERROR = 402;

    ///////////////////////////服务器连接异常响应码///////////////////////////////

    /**
     * 未知错误
     */
    public static final int UNKNOW = -100;
    /**
     * 网络没有开启
     */
    public static final int SERV_NET_ERROR = -101;
    /**
     *
     */
    public static final int SERV_ILLEGAL_ERROR = -102;
    /**
     * service 不是 服务器的实例
     */
    public static final int SERVICE_INSTANCE_FAILE = -103;
    /**
     * 网络请求超时
     */
    public static final int SERV_NET_TIME_OUT = -104;
    /**
     * 取消请求
     */
    public static final int CANCEL = -105;
    /**
     * json解析异常
     */
    public static final int JSON_ERROR = -106;
    /**
     * 无效
     */
    public static final int MOVED_PERMANENTLY = -301;

    /**
     * 不存在
     */
    public static final int NOT_FOUND = -404;
    /**
     * 服务端内部错误,接口平台异常
     */
    public static final int INTERNAL_SERVER_ERROR = -500;
    /**
     * 服务端内部错误,后端接口异常
     */
    public static final int INTERNAL_SERVER_ERROR_BEHIND = -502;


    ///////////////////////////业务异常常响应码///////////////////////////////



    public static boolean isNetError(int code) {
        switch (code) {
            case SERV_NET_ERROR:
                return true;
            default:
                return false;
        }
    }

    public static String getServExceptionMessage(int code) {
        String msg = "";
        switch (code) {
            case MOVED_PERMANENTLY:
                msg = "该服务已经暂停使用,请更新至最新版本";
                break;

            case NOT_FOUND:
                /**
                 * 不存在
                 */
                msg = "服务器未响应";
                break;
            case INTERNAL_SERVER_ERROR:
            case INTERNAL_SERVER_ERROR_BEHIND:
                msg = "服务器无法连接，请稍后再试";
                break;
            case SERV_NET_ERROR:
                msg = "服务器无法连接，请检查您的网络是否正常！";
                break;
            case SERV_NET_TIME_OUT:
                msg = "网络连接超时，请重试";
                break;
            case JSON_ERROR:
                msg = "数据解析异常";
                break;

            case SERV_ILLEGAL_ERROR:
                msg = "参数格式不正确";
                break;
            case UNKNOW:
                msg = "";
                break;
            case SERVICE_INSTANCE_FAILE:
                msg = "service不是服务器的实例";
                break;
            case CANCEL:
                msg = "请求被取消";
                break;

            default:
                break;
        }
        return msg;
    }
}
