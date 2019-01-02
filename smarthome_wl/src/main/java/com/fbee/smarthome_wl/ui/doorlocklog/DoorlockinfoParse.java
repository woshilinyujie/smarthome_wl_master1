package com.fbee.smarthome_wl.ui.doorlocklog;


import android.content.Context;
import android.text.TextUtils;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DoorLockGlobal;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.response.DoorAlarmRecordinfo;
import com.fbee.smarthome_wl.response.DoorRecordInfo;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 解析门锁信息工具类
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/3 16:50
 */
public class DoorlockinfoParse {

    public static final String DOOR_TYPE_RECORD ="1"; //门锁消息
    public static final String DOOR_TYPE_POWER ="2";  //低电量
    public static final String DOOR_TTPE_ALARM = "3"; //警报
    private DoorlockLogPresenter presenter;

    public DoorlockinfoParse() {
    }
    public DoorlockinfoParse(DoorlockLogPresenter presenter) {
        this.presenter=presenter;
    }

    /**
     * 请求添加用户设备到本地服务器
     */
    public void reqAddDeviceUser(String  ieee,String userId,String alairs,DoorlockLogPresenter presenter){
        AddDeviceUser body=new AddDeviceUser();
        body.setVendor_name("feibee");
        body.setUuid(ieee);
        AddDeviceUser.DeviceUserBean deviceUserBean=new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(userId);
        deviceUserBean.setNote(alairs);
       /* List<String> noticeList=new ArrayList<>();
        noticeList.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean.setWithout_notice_user_list(noticeList);*/
        body.setDevice_user(deviceUserBean);
        presenter.reqAddDeviceUser(body);

    }
    /**
     *  开锁记录
     * @param record
     * @param mdeviceUid
     * @param mdeviceName
     * @return
     */
    public List<Doorlockrecord> getDoorLockRecord(DoorRecordInfo record,
                                                  int mdeviceUid, String mdeviceName,String mdeviceIeee) {
        List<Doorlockrecord>  infolist= new ArrayList<Doorlockrecord>();
        if (record == null || record.getTime() ==null || record.getTime().size() ==0) {
            return infolist;
        }
        try {
            List<Integer> value = record.getValue();
            List<String> time = record.getTime();
            List<Integer> operation = record.getOperation();
            List<Integer> userId =  record.getUserID();
            List<Integer> attData = record.getAttData();
            if (value.size() == time.size()
                    && time.size() == userId.size()) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");

                for (int i = 0; i < time.size(); i++) {
                    Doorlockrecord info = new Doorlockrecord();
                    long jsonobj = Long.parseLong(time.get(i));
                    String dateTime = sdf.format(new Date(jsonobj * 1000L));
                    String message = null;
                    int id = userId.get(i);
                    int LockStatusByte = attData.get(i);
                    int way = operation.get(i);
                    byte byte3 = (byte) value.get(i).intValue();
                    //List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNumsNet= AppContext.getMap().get(String.valueOf(mdeviceUid));
                    String alias=UserDbUtil.getIns().getUserAliasByID(id,mdeviceUid);
                    String alamName =null;
                    if(TextUtils.isEmpty(alias))
                    {
                        alamName = id + "号用户";
                    }else{
                        alamName = alias;
                    }

                    // 进入管理员菜单 // 通过门锁状态解析出，文字描述，通过三个字段：
                    // LockStatusByte:门锁状态字节， way:开门方式，指纹、密码、刷卡等，
                    // Byte[3]:开门、关门、非法操作报警
                    if ((LockStatusByte & 0x08) == 8) {
                        // 密码进入管理员菜单
                        switch (way) {
                            case 0:
                                message = enter_Menu(LockStatusByte, alamName, "密码");
                                break;
                            case 2:
                                message = enter_Menu(LockStatusByte, alamName,
                                        "指纹");
                                break;
                            case 3:
                                message = enter_Menu(LockStatusByte, alamName,
                                        "刷卡");
                                break;
                            case 5:
                                message = enter_Menu(LockStatusByte, alamName,
                                        "多重");
                                break;

                        }

                        if ((LockStatusByte & 0xbf) == 0x88) {
                            message = alamName + "-胁迫报警进入菜单";
                        }// 胁迫指纹进入菜单（双人模式）
                        else if ((LockStatusByte & 0xbf) == 0x98) {
                            message = alamName + "-胁迫报警进入菜单(双人模式)";
                        }// 胁迫报警启用常开（菜单）
                        else if ((LockStatusByte & 0xbf) == 0x89) {

                            message = alamName + "-胁迫报警启用常开(菜单)";

                        }// 胁迫报警取消常开（菜单）
                        else if ((LockStatusByte & 0xbf) == 0x8a) {
                            message = alamName + "-胁迫报警取消常开(菜单)";

                        }
                        // 胁迫指纹进入菜单取消常开（双人模式）
                        else if ((LockStatusByte & 0xbf) == 0x9a) {
                            message = alamName + "-胁迫报警取消常开(菜单)(双人)";
                        }// 胁迫指纹进入菜单启用常开（双人模式）
                        else if ((LockStatusByte & 0xbf) == 0x99) {
                            message = alamName + "-胁迫报警启用常开(菜单)(双人)";
                        }
                    } else {// 开锁
                        if (byte3 == 0x02) { // 01：关门 02：开门 03：非法操作报警 05：非法卡)
                            // 刷卡
                            if (way == 3) {
                                message = Unlocking(LockStatusByte, mdeviceName, alamName, "刷卡");
                            }
                            // 多重验证
                            else if (way == 5) {
                                message = Unlocking(LockStatusByte, mdeviceName, alamName,
                                        "多重验证");
                            }
                            // 密码
                            else if (way == 0) {
                                message = Unlocking(LockStatusByte, mdeviceName, alamName, "密码");
                            }
                            // 指纹
                            else if (way == 2) {
                                message = Unlocking(LockStatusByte, mdeviceName, alamName, "指纹");
                            }// 远程
                            else if (way == 4) {
                                message = alamName + "-远程开锁";
                            }
                            if ((LockStatusByte & 0xbf) == 0x80) {

                                message = alamName + "-胁迫报警";

                            } else if ((LockStatusByte & 0xbf) == 0x90) {
                                message = alamName + "-胁迫报警 (双人模式)";
                            } else if ((LockStatusByte & 0xbf) == 0x82) {
                                message = alamName + "-胁迫报警取消常开";
                            } else if ((LockStatusByte & 0xbf) == 0x81) {

                                message = alamName + "-胁迫报警启用常开";

                            } else if ((LockStatusByte & 0xbf) == 0x92) {
                                message = alamName + "-胁迫报警取消常开(双人模式)";
                            } else if ((LockStatusByte & 0xbf) == 0x91) {
                                message = alamName + "-胁迫报警启用常开(双人模式)";

                            }

                        }// 非法操作报警
                        else if (byte3 == 0x03) {
                            message = "非法操作";
                        }
                    }

                    if (way == DoorLockGlobal.R_DOOR_LOCK_ALARM_FLAG) { // 防拆 报警各种报警
                        message = getTamperString(byte3);
                    }

                    if (id >=0 && !UserDbUtil.getIns().queryUserIsExist(id, mdeviceUid)) {

                        User user = new User(id,mdeviceUid,alamName);
                        UserDbUtil.getIns().insert(user);
                    }


                    if(!String.valueOf(id).isEmpty()){
                        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums02= AppContext.getMap().get(String.valueOf(mdeviceUid));

                        if(userNums02!=null){
                            boolean tag=false;
                            for (int j = 0; j <userNums02.size() ; j++) {
                                if(userNums02.get(j).getId().equals(String.valueOf(id))){
                                    tag=true;
                                    break;
                                }
                            }
                            if(!tag){
                                //添加设备用户到服务器
                                reqAddDeviceUser(mdeviceIeee,String.valueOf(id),alamName,presenter);

                            }
                        }else{
                            //添加设备用户到服务器
                            reqAddDeviceUser(mdeviceIeee,String.valueOf(id),alamName,presenter);

                        }
                    }


                    info.setMsg(message);
                    info.setMsgType(DOOR_TYPE_RECORD);
                    info.setDeviceID(mdeviceUid);
                    info.setTime(dateTime);
                    info.setUserID(id);
                    infolist.add(info);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return infolist;

    }


    /**显示不同的信息*/
    public String getTamperString(byte alarmCode){
        String str="";
        Context mContext = BaseApplication.getInstance().getContext();
        switch (alarmCode) {
            case 0:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_4);
                break;
            case 4:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_0);
                break;
            case 5:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_1);
                break;
            case 6:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_2);
                break;
            case 7:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_3);
                break;
            case 51:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_5);
                break;
            case (byte) 0x85:
                str="门锁已关闭";
                break;
            case (byte) 0x87:
                str="门锁已关闭";
                break;
            default:
                break;
        }
        return str;
    }




    private String Unlocking(int LockStatusByte, String deviceName, String userName,
                             String str) {
        String msg = null;
        if (LockStatusByte == 0) {
            msg = userName + "-" + str + "开锁";
        } else if ((LockStatusByte & 0xbf) == 0x10) {
            msg = userName + "-" + str + "开锁 (双人模式)";


        } else if ((LockStatusByte & 0xbf) == 0x02) {
            msg = userName + "-" + str + "开锁取消常开";


        } else if ((LockStatusByte & 0xbf) == 0x01) {
            msg = userName + "-" + str + "开锁启用常开";


        } else if ((LockStatusByte & 0xbf) == 0x12) {
            msg = userName + "-" + str + "开锁取消常开(双人模式)";

        } else if ((LockStatusByte & 0xbf) == 0x11) {
            msg = userName + "-" + str + "开锁启用常开(双人模式)";

        }else{

            if((LockStatusByte & 0xbf) != 0x80&&(LockStatusByte & 0xbf) != 0x90&&(LockStatusByte & 0xbf) != 0x82&&(LockStatusByte & 0xbf) != 0x81&&(LockStatusByte & 0xbf) != 0x91&&(LockStatusByte & 0xbf) != 0x92){
                msg = userName + "-"+str+ "开锁";
            }
        }

        return msg;
    }

    private String enter_Menu(int LockStatusByte, String userName, String str) {
        String msg = null;
        if (LockStatusByte == 8) {
            msg = userName + "-" + str + "验证进入菜单";
        } else if (LockStatusByte == 0x18) {
            msg = userName + "-" + str + "验证进入菜单(双人模式)";
        } else if (LockStatusByte == 10) {
            msg = userName + "-取消常开(菜单)";
        } else if (LockStatusByte == 9) {
            msg = userName + "-启用常开(菜单)";
        } else if (LockStatusByte == 26) {
            msg = userName + "-取消常开(菜单)(双人)";
        } else if ((LockStatusByte & 0xbf) == 0x19) {
            msg = userName + "-启用常开(菜单)(双人)";
        }
        return msg;

    }


    /**
     * 获取电量信息
     * @param record
     * @param mdeviceUid
     * @return
     */
    public List<Doorlockrecord> getDoorLockPowerRecord(DoorlockpowerInfo record, int mdeviceUid) {
        List<Doorlockrecord> infos = new ArrayList<Doorlockrecord>();
        try {

            List<String> time = record.getTime();
            List<String> value = record.getValue();
            String message = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < value.size(); i++) {
                Doorlockrecord info = new Doorlockrecord();
                long jsonobj = Long.parseLong(time.get(i));
                String dateTime = sdf.format(new Date(jsonobj * 1000L));
                if (Integer.parseInt(value.get(i))==50) {
                    message = "低电压报警";
                    info.setDeviceID(mdeviceUid);
                    info.setMsgType(DOOR_TYPE_POWER);
                    info.setMsg(message);
                    info.setTime(dateTime);
                    infos.add(info);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }


    /**
     * 报警信息
     * @param record
     * @param mdeviceUid
     * @return
     */
    public List<Doorlockrecord> getDoorLockArmRecord(DoorAlarmRecordinfo record, int mdeviceUid) {
        List<Doorlockrecord> infos = new ArrayList<Doorlockrecord>();
        try {

            List<String> time = record.getTime();
            List<Integer> value = record.getValue();
            List<Integer> operation= record.getOperation();
            List<Integer> clusterID=  record.getClusterID();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < time.size(); i++) {
                if (clusterID.get(i) == 257) {// 门锁警报
                    Doorlockrecord info = new Doorlockrecord();
                    long jsonobj = Long.parseLong(time.get(i));
                    String dateTime = sdf.format(new Date(jsonobj * 1000L));
                    String message = null;

                    switch ((byte) value.get(i).intValue()) {
                        case 0:
                            message = "取消报警";
                            break;
                        case 4:
                            message = "防拆报警";
                            break;
                        case 5:
                            message = "未关锁报警";
                            break;
                        case 6:
                            message = "胁迫报警";
                            break;
                        case 7:
                            message = "假锁报警";
                            break;
                        case 51:
                            message = "非法操作";
                            break;
                        case (byte) 0x85:
                        case (byte) 0x87:
                            message = "门锁已关闭";
                            break;
                    }
                    if(message!=null){
                        info.setDeviceID(mdeviceUid);
                        info.setMsg(message);
                        info.setMsgType(DOOR_TTPE_ALARM);
                        info.setTime(dateTime);
                        infos.add(info);
                    }


                } else if (clusterID.get(i) == 1) {// 电池警报

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }



}
