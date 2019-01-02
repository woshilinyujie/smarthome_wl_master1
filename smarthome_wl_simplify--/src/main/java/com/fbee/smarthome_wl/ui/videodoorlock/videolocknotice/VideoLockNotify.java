package com.fbee.smarthome_wl.ui.videodoorlock.videolocknotice;

import android.content.Context;
import android.content.Intent;

import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.videolockreq.DeviceOpenVideoLockReq;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.widget.dialog.AlamDialogActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by WLPC on 2017/10/13.
 */

public class VideoLockNotify {
    private Context context;
    private DeviceOpenVideoLockReq request;
    private StringBuffer stringBuffer;

    public VideoLockNotify(Context context, DeviceOpenVideoLockReq response) {
        this.context = context;
        this.request = response;
    }

//    public void handleVideoLockNotify() {
//        if (response.getUuid() != null) {
//            if (response.getData() != null) {
//                if (response.getData().getDevice_user_id() != null) {
//                    boolean isAssoiateUser = isAssociateDevicesUser(response.getUuid(), response.getData().getDevice_user_id());
//                    if (isAssoiateUser) {
//
//                    } else {
//                        stringBuffer = new StringBuffer();
//                        String title = response.getData().getNote();
//                        if (title == null) {
//                            title = "视频锁";
//                        }
//                        String device_user_id = response.getData().getDevice_user_id();
//                        stringBuffer.append(device_user_id + "号用户");
//                        /***
//                         * 是否被胁迫，true false
//                         */
//                        String stress_status = response.getData().getStress_status();
//                        SelectCharacter(stress_status);
//                        /***
//                         * 开锁的方式，pwd (密码) fp (指纹) card (刷卡) face (人脸)
//                         * rf (感应) eye (虹膜) vena (指静脉) remote (远程)
//                         */
//                        String unlock_mode = response.getData().getUnlock_mode();
//                        SelectCharacter(unlock_mode);
//                        /***
//                         * 验证的方式，sgl (单人) dbl (双人) mutil (多人)
//                         */
//                        String auth_mode = response.getData().getAuth_mode();
//                        SelectCharacter(auth_mode);
//                        /***
//                         * enter_menu (进入菜单) off_lock (取消常开) on_lock (启用常开)
//                         * default (恢复出厂设置)on_infra (启用红外报警) off_infra (取消红外报警)
//                         */
//                        String op_type = response.getData().getOp_type();
//                        SelectCharacter(op_type);
//                        Intent intent = new Intent(context,
//                                AlamDialogActivity.class);
//                        intent.putExtra("isAlarm", isAlarm);
//                        intent.putExtra("name", title);
//                        intent.putExtra("str", stringBuffer.toString());
//                        context.startActivity(intent);
//                    }
//                }
//            }
//        }
//    }

    boolean isAlarm = false;

    private String SelectCharacter(String alarm_message) {
        switch (alarm_message) {
            /***
             * 是否被胁迫，true false
             */
            case "true":
                stringBuffer.append("胁迫");
                isAlarm = true;
                break;
            /***
             * 开锁的方式，pwd (密码) fp (指纹) card (刷卡) face (人脸)
             * rf (感应) eye (虹膜) vena (指静脉) remote (远程)
             */
            case "pwd":
                stringBuffer.append("密码");
                break;
            case "fp":
                stringBuffer.append("指纹");
                break;
            case "card":
                stringBuffer.append("刷卡");
                break;
            case "face":
                stringBuffer.append("人脸");
                break;
            case "rf":
                stringBuffer.append("感应");
                break;
            case "eye":
                stringBuffer.append("虹膜");
                break;
            case "vena":
                stringBuffer.append("指静脉");
                break;
            case "remote":
                stringBuffer.append("远程");
                break;
            /***
             * 验证的方式，sgl (单人) dbl (双人) mutil (多人)
             */
            case "sgl":
                stringBuffer.append("单人");
                break;
            case "dbl":
                stringBuffer.append("双人");
                break;
            case "mutil":
                stringBuffer.append("多人");
                break;
            /***
             * enter_menu (进入菜单) off_lock (取消常开) on_lock (启用常开)
             * default (恢复出厂设置)on_infra (启用红外报警) off_infra (取消红外报警)
             */
            case "enter_menu":
                stringBuffer.append("进入菜单");
                break;
            case "off_lock":
                stringBuffer.append("取消常开");
                break;
            case "on_lock":
                stringBuffer.append("启用常开");
                break;
            case "default":
                stringBuffer.append("恢复出厂设置");
                break;
            case "on_infra":
                stringBuffer.append("启用红外报警");
                break;
            case "off_infra":
                stringBuffer.append("取消红外报警");
                break;
        }
        return stringBuffer.toString();
    }

    /**
     * 判断是否是绑定设备用户
     *
     * @param userNum
     * @return
     */
    public boolean isAssociateDevicesUser(String uid, String userNum) {
        HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> userEntityMap = AppContext.getMap();
        String phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        if (userEntityMap != null) {
            if (userEntityMap.size() > 0) {
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = userEntityMap.get(uid);
                if (bodyEntities != null) {
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        if (bodyEntities.get(i).getId().equals(userNum)) {
                            List<String> userList = bodyEntities.get(i).getWithout_notice_user_list();
                            if (userList != null && userList.size() > 0) {
                                for (int j = 0; j < userList.size(); j++) {
                                    if (phone.equals(userList.get(j))) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public void handleMsg(){
        String name = request.getUuid() + " ";
        if (!StringUtils.isEmpty(request.getData().getNote())) {
            name = request.getData().getNote() + " ";
        }
        String userName = request.getData().getDevice_user_id()+ "号用户";
        if(!StringUtils.isEmpty(request.getData().getDevice_user_note())){
            userName=request.getData().getDevice_user_note();
        }
        StringBuffer stringBuffer=new StringBuffer();
        boolean isAssoiateUser = isAssociateDevicesUser(request.getUuid(), request.getData().getDevice_user_id());
        if (isAssoiateUser) {
            try {
                if (request.getData().getOp_type().equals("enter_menu") && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_1);
                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_2);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_3);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_4);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_5);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_6);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_7);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_8);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_9);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_10);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_11);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_12);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_13);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_14);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_15);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_16);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_17);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_18);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_19);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_20);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_21);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_22);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_23);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_24);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_25);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_26);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_27);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_28);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_29);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_30);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_31);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_32);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_33);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_34);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_35);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_36);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_37);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_38);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_39);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_40);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_41);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_42);

                }
                if(!StringUtils.isEmpty(stringBuffer.toString())){
                    Intent intent = new Intent(context,
                            AlamDialogActivity.class);
                    intent.putExtra("isAlarm", isAlarm);
                    intent.putExtra("name", name);
                    intent.putExtra("str", stringBuffer.toString());
                    context.startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                if (request.getData().getOp_type().equals("enter_menu") && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_1);
                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_2);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_3);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_4);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_5);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_6);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_7);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_8);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_9);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_10);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_11);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_12);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_13);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_14);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_15);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_16);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_17);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_18);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_19);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_20);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_21);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_22);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_23);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_24);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_25);

                } else if (request.getData().getOp_type().equals("unlock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_26);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_27);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_28);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_29);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_30);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_31);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_32);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_33);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(userName).append(unlock_34);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_35);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_36);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_37);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_38);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("pwd")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_39);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("fp") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_40);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("card")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_41);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getUnlock_mode().equals("remote")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("false")) {
                    stringBuffer.append(unlock_42);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_43);

                } else if (request.getData().getOp_type().equals("enter_menu")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_44);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_45);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_46);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_47);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_48);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_49);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_50);

                } else if (request.getData().getOp_type().equals("off_lock")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_51);

                } else if (request.getData().getOp_type().equals("on_lock")
                        && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_52);

                } else if (request.getData().getOp_type().equals("unlock") && request.getData().getAuth_mode().equals("sgl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(userName).append(unlock_53);

                } else if (request.getData().getOp_type().equals("unlock") && request.getData().getAuth_mode().equals("dbl")
                        && request.getData().getStress_status().equals("true")) {
                    stringBuffer.append(unlock_54);

                }
                if(!StringUtils.isEmpty(stringBuffer.toString())){
                    Intent intent = new Intent(context,
                            AlamDialogActivity.class);
                    intent.putExtra("isAlarm", isAlarm);
                    intent.putExtra("name", name);
                    intent.putExtra("str", stringBuffer.toString());
                    context.startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    private String unlock_1="密码验证进入菜单";
    private String unlock_2="指纹验证进入菜单";
    private String unlock_3="刷卡验证进入菜单";
    private String unlock_4="密码验证进入菜单(双人模式)";
    private String unlock_5="指纹验证进入菜单(双人模式)";
    private String unlock_6="刷卡验证进入菜单(双人模式)";
    private String unlock_7="密码取消常开";
    private String unlock_8="指纹取消常开";
    private String unlock_9="刷卡取消常开";
    private String unlock_10="密码启用常开";
    private String unlock_11="指纹启用常开";
    private String unlock_12="刷卡启用常开";
    private String unlock_13="密码取消常开(双人模式)";
    private String unlock_14="指纹取消常开(双人模式)";
    private String unlock_15="刷卡取消常开(双人模式)";
    private String unlock_16="密码启用常开(双人模式)";
    private String unlock_17="指纹启用常开(双人模式)";
    private String unlock_18="刷卡启用常开(双人模式)";
    private String unlock_19="密码开锁";
    private String unlock_20="指纹开锁";
    private String unlock_21="刷卡开锁";
    private String unlock_22="远程开锁";
    private String unlock_23="密码开锁(双人模式)";
    private String unlock_24="指纹开锁(双人模式)";
    private String unlock_25="刷卡开锁(双人模式)";
    private String unlock_26="远程开锁(双人模式)";
    private String unlock_27="密码开锁取消常开";
    private String unlock_28="指纹开锁取消常开";
    private String unlock_29="刷卡开锁取消常开";
    private String unlock_30="远程开锁取消常开";
    private String unlock_31="密码开锁启动常开";
    private String unlock_32="指纹开锁启动常开";
    private String unlock_33="刷卡开锁启动常开";
    private String unlock_34="远程开锁启动常开";
    private String unlock_35="密码开锁取消常开(双人模式)";
    private String unlock_36="指纹开锁取消常开(双人模式)";
    private String unlock_37="刷卡开锁取消常开(双人模式)";
    private String unlock_38="远程开锁取消常开(双人模式)";
    private String unlock_39="密码开锁启动常开(双人模式)";
    private String unlock_40="指纹开锁启动常开(双人模式)";
    private String unlock_41="刷卡开锁启动常开(双人模式)";
    private String unlock_42="远程开锁启动常开(双人模式)";
    private String unlock_43="胁迫报警进入菜单";
    private String unlock_44="胁迫报警进入菜单(双人模式)";
    private String unlock_45="胁迫报警取消常开";
    private String unlock_46="胁迫报警启动常开";
    private String unlock_47="胁迫报警取消常开(双人模式)";
    private String unlock_48="胁迫报警启动常开(双人模式)";
    private String unlock_49="胁迫报警取消常开";
    private String unlock_50="胁迫报警启动常开";
    private String unlock_51="胁迫报警取消常开(双人模式)";
    private String unlock_52="胁迫报警启动常开(双人模式)";
    private String unlock_53="胁迫报警";
    private String unlock_54="胁迫报警(双人模式)";
}
