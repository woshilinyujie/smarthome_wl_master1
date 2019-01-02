package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.view.CircleView;
import com.fbee.smarthome_wl.widget.dialog.ColorSelectDialog;
import com.fbee.smarthome_wl.widget.dialog.DialogfourMin;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceData;

import java.util.List;

import static com.fbee.smarthome_wl.R.array.minute;
import static com.fbee.smarthome_wl.R.id.iv_blue;
import static com.fbee.smarthome_wl.R.id.iv_green;
import static com.fbee.smarthome_wl.R.id.iv_orange;
import static com.fbee.smarthome_wl.R.id.iv_palette;
import static com.fbee.smarthome_wl.R.id.iv_red;
import static com.fbee.smarthome_wl.R.id.switch2;


/**
 * 场景中设备listView的Adapter
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/27 10:03
 */
public class ScenariioEditAdapter extends BaseAdapter {
    private Context mContext;
    private List<SenceData> mDatas;

    private OnSwitchListener switchListener;
    private OnStopSeekBarTouch seekbarStopListener;
    private OnDelayListener  onDelayListener;

    private static final int TYPE_DEFAULT =0; //普通设备布局
    private static final int TYPE_COLOR =1;  //调色灯布局
    private static final int TYPE_LIGHT =2;  //调光灯布局

    ViewHolder holder = null;
    ViewHolderColorLight colorholder = null;
    ViewHolderDimmerLight dimmerholder = null;
    boolean flag = true;
    boolean colorFlag =true;
    private ArrayMap<Integer,Boolean>  colormap =new ArrayMap<Integer,Boolean>();
    private ArrayMap<Integer,Boolean>  tempmap =new ArrayMap<Integer,Boolean>();
    private ArrayMap<Integer,Boolean>  defaultmap =new ArrayMap<Integer,Boolean>();
   private ColorSelectDialog dialog;


    public void remove(SenceData data){
        try{
            mDatas.remove(data);
        }catch (Exception e){

        }
    }

    public ScenariioEditAdapter(Context mContext, List<SenceData> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;

    }

    public void setSwitchListener(OnSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    public void setOnDelayListener(OnDelayListener onDelayListener) {
        this.onDelayListener = onDelayListener;
    }

    public void setSeekbarStopListener(OnStopSeekBarTouch seekbarStopListener) {
        this.seekbarStopListener = seekbarStopListener;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public SenceData getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final SenceData info = mDatas.get(position);
        int type = getItemViewType(position);

        if (convertView == null ) {

            switch (type){
                case TYPE_DEFAULT:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_ordinary_light, null);
                    holder.name = (TextView) convertView.findViewById(R.id.tv_device_name);
                    holder.aSwitch = (ToggleButton) convertView.findViewById(R.id.switch1);
                    holder.iv_picture = (ImageView) convertView.findViewById(R.id.iv_device);
                    holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
                    holder.relativeLayout  = (RelativeLayout) convertView.findViewById(R.id.layout_delaytime);
                    holder.switchdelay = (ToggleButton) convertView.findViewById(switch2);
                    holder.time = (TextView) convertView.findViewById(R.id.tv_delaytime);
                    convertView.setTag(holder);
                    break;
                case TYPE_COLOR:
                    colorholder = new ViewHolderColorLight();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_color_lamp, null);
                    colorholder.name = (TextView) convertView.findViewById(R.id.tv_device_name);
                    colorholder.aSwitch = (ToggleButton) convertView.findViewById(R.id.switch_light);
                    colorholder.brightness = (LinearLayout) convertView.findViewById(R.id.layout_brightness);
                    colorholder.colotlayout = (LinearLayout) convertView.findViewById(R.id.layout_hide);
                    colorholder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
                    colorholder.seekBar_light = (SeekBar) convertView.findViewById(R.id.seekBar_light);
                    colorholder.iv_color = (CircleView) convertView.findViewById(R.id.iv_color);
                    //红色
                    colorholder.iv_red = (ImageView) convertView.findViewById(iv_red);
                    colorholder.iv_blue = (ImageView) convertView.findViewById(iv_blue);
                    colorholder.iv_green = (ImageView) convertView.findViewById(iv_green);
                    colorholder.iv_orange =  (ImageView) convertView.findViewById(iv_orange);
                    colorholder.iv_slateblue =(ImageView) convertView.findViewById(R.id.iv_slatebule);
                    colorholder.iv_palette =(ImageView) convertView.findViewById(iv_palette);

                    colorholder.layoutDelaytime = (RelativeLayout) convertView.findViewById(R.id.layout_delaytime);
                    colorholder.switch2 = (ToggleButton) convertView.findViewById(switch2);
                    colorholder.tvDelaytime = (TextView) convertView.findViewById(R.id.tv_delaytime);

                    convertView.setTag(colorholder);
                    break;
                case TYPE_LIGHT:
                    dimmerholder = new ViewHolderDimmerLight();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_dimming_light, null);
                    dimmerholder.name = (TextView) convertView.findViewById(R.id.tv_device_name);
                    dimmerholder.aSwitch = (ToggleButton) convertView.findViewById(R.id.switch1);
                    dimmerholder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
                    dimmerholder.layout = (LinearLayout) convertView.findViewById(R.id.animalayout_light);
                    dimmerholder.layout_click = (RelativeLayout) convertView.findViewById(R.id.layout_click);
                    dimmerholder.seekBar_light = (SeekBar) convertView.findViewById(R.id.seekBar_light);
                    dimmerholder.brightness = (LinearLayout) convertView.findViewById(R.id.layout_brightness);
                    dimmerholder.temperature = (LinearLayout) convertView.findViewById(R.id.layout_temperature);
                    dimmerholder.seekBar_temperature = (SeekBar) convertView.findViewById(R.id.seekBar_temperature);

                    dimmerholder.layoutDelaytime = (RelativeLayout) convertView.findViewById(R.id.layout_delaytime);
                    dimmerholder.switch2 = (ToggleButton) convertView.findViewById(R.id.switch2);
                    dimmerholder.tvDelaytime = (TextView) convertView.findViewById(R.id.tv_delaytime);


                    convertView.setTag(dimmerholder);
                    break;

            }


        }

            switch (type){
                case TYPE_DEFAULT:
                    holder = (ViewHolder) convertView.getTag();
                    holder.name.setText(getDeviceName(info.getuId()));
                    holder.aSwitch.setOnCheckedChangeListener(null);
                    if(info.getData1() ==1){
                        holder.aSwitch.setChecked(true);
                    }else if(0 == info.getData1()){
                        holder.aSwitch.setChecked(false);
                    }

                    if(info.getDelaytime()>0){
                        holder.time.setVisibility(View.VISIBLE);
                        holder.time.setText("时间"+info.getDelaytime()+"秒");
                        holder.switchdelay.setOnCheckedChangeListener(null);
                        holder.switchdelay.setChecked(true);
                   }else{
                        holder.time.setVisibility(View.GONE);
                        holder.switchdelay.setOnCheckedChangeListener(null);
                        holder.switchdelay.setChecked(false);
                    }


                    if(defaultmap.get(position) != null && defaultmap.get(position)){
                        holder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                        holder.relativeLayout.setVisibility(View.VISIBLE);
                    }else{
                        holder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                        holder.relativeLayout.setVisibility(View.GONE);
                    }


                    holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            switchListener.onSwitchInteraction(buttonView,isChecked,position);
                        }
                    });

                    holder.iv_arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(defaultmap.get(position) == null || defaultmap.get(position)){
                                holder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                                holder.relativeLayout.setVisibility(View.VISIBLE);
                                defaultmap.put(position,false);
                            }else{
                                holder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                                holder.relativeLayout.setVisibility(View.GONE);
                                defaultmap.put(position,true);
                            }
                            notifyDataSetChanged();
                        }
                    });


                    holder.switchdelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                new DialogfourMin(mContext, new DialogfourMin.OnChooseListener() {
                                    @Override
                                    public void onTimeCallback( int second) {
                                        if(second >0){
                                            holder.time.setVisibility(View.VISIBLE);
                                            holder.time.setText("时间"+second+"秒");
                                            info.setDelaytime((byte)(second));
                                            if(onDelayListener != null){
                                                onDelayListener.onDelaytime(position,second);
                                            }
                                            notifyDataSetChanged();
                                        }

                                    }
                                }).show();
                            }else{
                                info.setDelaytime((byte)0);
                                holder.time.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }
                    });



                    switch (info.getDeviceId()){
                        //开关
                        case DeviceList.DEVICE_ID_SOCKET:
                            holder.iv_picture.setImageResource(R.mipmap.socket);
                            break;
                        //窗帘
                        case DeviceList.DEVICE_ID_CURTAIN :
                            holder.iv_picture.setImageResource(R.mipmap.blind);
                            break;
                        case DeviceList.DEVICE_ID_SWITCH:
                            holder.iv_picture.setImageResource(R.mipmap.mul_switch);
                            break;

                        default:
                            holder.iv_picture.setImageResource(R.mipmap.default_device);
                            break;

                    }


                    break;
                case TYPE_COLOR:
                    colorholder=  (ViewHolderColorLight)convertView.getTag();
                    colorholder.name.setText(getDeviceName(info.getuId()));
                    colorholder.aSwitch.setOnCheckedChangeListener(null);
                    if(info.getData1() ==1){
                        colorholder.aSwitch.setChecked(true);
                    }else if(0 == info.getData1()){
                        colorholder.aSwitch.setChecked(false);
                    }

                    if(info.getDelaytime()>0){
                        colorholder.tvDelaytime.setVisibility(View.VISIBLE);
                        colorholder.tvDelaytime.setText("时间"+info.getDelaytime()+"秒");
                        colorholder.switch2.setOnCheckedChangeListener(null);
                        colorholder.switch2.setChecked(true);
                    }else{
                        colorholder.tvDelaytime.setVisibility(View.GONE);
                        colorholder.switch2.setOnCheckedChangeListener(null);
                        colorholder.switch2.setChecked(false);
                    }

                    if(null == colormap.get(position) || colormap.get(position)){
                        colorholder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                        colorholder.brightness.setVisibility(View.GONE);
                        colorholder.colotlayout.setVisibility(View.GONE);
                        colorholder.layoutDelaytime.setVisibility(View.GONE);
                    }else{
                        colorholder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                        colorholder.brightness.setVisibility(View.VISIBLE);
                        colorholder.colotlayout.setVisibility(View.VISIBLE);
                        colorholder.layoutDelaytime.setVisibility(View.VISIBLE);
                    }

                    if(info.getData2() >=0){
                        colorholder.seekBar_light.setProgress(info.getData2());
                    }else{
                        colorholder.seekBar_light.setProgress(127-info.getData2());
                    }

                    float[] arrayOfObject2 = new float[3];
                    if(info.getData3()>0){
                        arrayOfObject2[0] = info.getData3()*360.0f/255.0f;
                    }else{
                        arrayOfObject2[0] =(256+info.getData3())*360.0f/255.0f;
                    }

                    if(info.getData4()>=0){
                        arrayOfObject2[1] = info.getData4()/254.0f;
                    }else{
                        arrayOfObject2[1] = (256+info.getData4())/254.0f;
                    }

                    arrayOfObject2[2] = 1.0f;


                    colorholder.iv_color.setColor( Color.HSVToColor(arrayOfObject2));

                    colorholder.iv_arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(null == colormap.get(position) || colormap.get(position)){
                                colorholder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                                colorholder.brightness.setVisibility(View.GONE);
                                colorholder.colotlayout.setVisibility(View.GONE);
                                colorholder.layoutDelaytime.setVisibility(View.GONE);
                                colorFlag = false;
                                colormap.put(position,colorFlag);

                            }else{
                                colorholder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                                colorholder.brightness.setVisibility(View.VISIBLE);
                                colorholder.colotlayout.setVisibility(View.VISIBLE);
                                colorholder.layoutDelaytime.setVisibility(View.VISIBLE);
                                colorFlag = true;
                                colormap.put(position,colorFlag);
                            }
                            notifyDataSetChanged();
                        }
                    });


                    colorholder.switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                new DialogfourMin(mContext, new DialogfourMin.OnChooseListener() {
                                    @Override
                                    public void onTimeCallback(int second) {
                                        if(minute>0 || second >0){
                                            colorholder.tvDelaytime.setVisibility(View.VISIBLE);
                                            colorholder.tvDelaytime.setText("时间"+second+"秒");
                                            info.setDelaytime((byte)(second));
                                            if(onDelayListener != null){
                                                onDelayListener.onDelaytime(position,second);
                                            }
                                            notifyDataSetChanged();
                                        }

                                    }
                                }).show();
                            }else{
                                info.setDelaytime((byte)0);
                                colorholder.tvDelaytime.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }
                    });


                    colorholder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            switchListener.onSwitchInteraction(buttonView,isChecked,position);
                        }
                    });
                    colorholder.seekBar_light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            seekbarStopListener.onStopLightTouch(seekBar.getProgress(),position);
                        }
                    });
                    colorholder.iv_red.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorholder.iv_color.setColor(Color.parseColor("#FF0000"));
                            setColor("FF0000",position);
                        }
                    });

                    colorholder.iv_blue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorholder.iv_color.setColor(Color.parseColor("#0000FF"));
                            setColor("0000FF",position);
                        }
                    });

                    colorholder.iv_slateblue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorholder.iv_color.setColor(Color.parseColor("#6A5ACD"));
                            setColor("6A5ACD",position);
                        }
                    });

                    colorholder.iv_orange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorholder.iv_color.setColor(Color.parseColor("#FFA500"));
                            setColor("FFA500",position);
                        }
                    });

                    colorholder.iv_green.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorholder.iv_color.setColor(Color.parseColor("#00FF00"));
                            setColor("00FF00",position);
                        }
                    });

                    colorholder.iv_palette.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if(null == dialog){
                                dialog = new ColorSelectDialog(mContext);
                                dialog.setOnColorSelectListener(new ColorSelectDialog.OnColorSelectListener() {
                                    @Override
                                    public void onSelectFinish(int color) {
                                        int red=Color.red(color);
                                        int green=Color.green(color);
                                        int blue=Color.blue(color);

                                        colorholder.iv_color.setColor(Color.rgb(red,green,blue));
                                        float[] arrayOfObject2 = new float[3];
                                        Color.colorToHSV(Color.rgb(red,green,blue), arrayOfObject2);
                                        byte hue = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
                                        byte sat = (byte)((int)(arrayOfObject2[1] * 254.0F));
                                        seekbarStopListener.onColorResult(hue,sat,position);


                                    }
                                });
                                dialog.show();
//                            }else{
//                                dialog.show();
//                            }
                        }
                    });


                    break;
                case TYPE_LIGHT:
                    dimmerholder = (ViewHolderDimmerLight)convertView.getTag();
                    dimmerholder.name.setText(getDeviceName(info.getuId()));
                    dimmerholder.aSwitch.setOnCheckedChangeListener(null);
                    if(info.getData1() ==1){
                        dimmerholder.aSwitch.setChecked(true);
                    }else if(0 == info.getData1()){
                        dimmerholder.aSwitch.setChecked(false);
                    }

                    if(info.getDelaytime()>0){
                        dimmerholder.tvDelaytime.setVisibility(View.VISIBLE);
                        dimmerholder.tvDelaytime.setText("时间"+info.getDelaytime()+"秒");
                        dimmerholder.switch2.setOnCheckedChangeListener(null);
                        dimmerholder.switch2.setChecked(true);
                    }else{
                        dimmerholder.tvDelaytime.setVisibility(View.GONE);
                        dimmerholder.switch2.setOnCheckedChangeListener(null);
                        dimmerholder.switch2.setChecked(false);
                    }

                    if(null == tempmap.get(position) || tempmap.get(position)){
                        dimmerholder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                        dimmerholder.brightness.setVisibility(View.GONE);
                        dimmerholder.temperature.setVisibility(View.GONE);
                        dimmerholder.layoutDelaytime.setVisibility(View.GONE);
                    }else{
                        dimmerholder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                        dimmerholder.brightness.setVisibility(View.VISIBLE);
                        dimmerholder.temperature.setVisibility(View.VISIBLE);
                        dimmerholder.layoutDelaytime.setVisibility(View.VISIBLE);
                    }

                    if(info.getData2()>=0){
                        dimmerholder.seekBar_light.setProgress(info.getData2());
                    }else{
                        dimmerholder.seekBar_light.setProgress(256+info.getData2());
                    }

                    if(info.getData4() >=0){
                        dimmerholder.seekBar_temperature.setProgress(info.getData4());
                    }else{
                        dimmerholder.seekBar_temperature.setProgress(256+info.getData4());
                    }


                    dimmerholder.switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                new DialogfourMin(mContext, new DialogfourMin.OnChooseListener() {
                                    @Override
                                    public void onTimeCallback( int second) {
                                        if(minute>0 || second >0){
                                            dimmerholder.tvDelaytime.setVisibility(View.VISIBLE);
                                            dimmerholder.tvDelaytime.setText("时间"+second+"秒");
                                            info.setDelaytime((byte)(second));
                                            if(onDelayListener != null){
                                                onDelayListener.onDelaytime(position,second);
                                            }
                                            notifyDataSetChanged();
                                        }

                                    }
                                }).show();
                            }else{
                                info.setDelaytime((byte)0);
                                dimmerholder.tvDelaytime.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        }
                    });


                    dimmerholder.iv_arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(tempmap.get(position) == null ||tempmap.get(position) ){
                                dimmerholder.iv_arrow.setImageResource(R.mipmap.arrow_down);
                                dimmerholder.brightness.setVisibility(View.GONE);
                                dimmerholder.temperature.setVisibility(View.GONE);
                                dimmerholder.layoutDelaytime.setVisibility(View.GONE);
                                flag = false;
                                tempmap.put(position,flag);
                            }else{
                                dimmerholder.iv_arrow.setImageResource(R.mipmap.arrow_up);
                                dimmerholder.brightness.setVisibility(View.VISIBLE);
                                dimmerholder.temperature.setVisibility(View.VISIBLE);
                                dimmerholder.layoutDelaytime.setVisibility(View.VISIBLE);
                                flag = true;
                                tempmap.put(position,flag);
                            }
                            notifyDataSetChanged(); //必须有的一步
                        }
                    });

                    dimmerholder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            switchListener.onSwitchInteraction(buttonView,isChecked,position);
                        }
                    });
                    //亮度
                    dimmerholder.seekBar_light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            seekbarStopListener.onStopLightTouch(seekBar.getProgress(),position);
                        }
                    });
                    //色温
                    dimmerholder.seekBar_temperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            seekbarStopListener.onStopTempTouch(seekBar.getProgress(),position);
                        }
                    });


                    break;

            }


        return convertView;
    }



    private String getDeviceName(int uid){
        List<DeviceInfo> devices = AppContext.getmOurDevices();
        for (int i = 0; i <devices.size() ; i++) {
            if(devices.get(i).getUId() ==uid){
                return devices.get(i).getDeviceName();
            }
        }
        return "";
    }


    private void setColor(String color,int position){
        float[] arrayOfObject2 = new float[3];
        Color.colorToHSV(Integer.parseInt(color, 16), arrayOfObject2);
        byte hue = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
        byte sat = (byte)((int)(arrayOfObject2[1] * 254.0F));
        seekbarStopListener.onColorResult(hue,sat,position);
        notifyDataSetChanged();
    }


    /**
     * 延时监听
     */
    public  interface  OnDelayListener{

        void onDelaytime( int position, int second );
    }

    /**
     * 开关监听
     */
    public interface OnSwitchListener {
        void onSwitchInteraction(CompoundButton compoundButton, boolean b, int position);
    }

    /**
     * seekbar
     */
    public interface  OnStopSeekBarTouch{
        //亮度
        void onStopLightTouch(int progress, int position);
        //色温
        void onStopTempTouch(int progress, int position);
        //颜色
        void onColorResult(byte colot,byte sat,int position);

    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }


    /**
     * 根据数据源的position返回需要显示的的layout的type
     * type的值必须从0开始
     *
     * */
    @Override
    public int getItemViewType(int position) {
        SenceData deviceInfo = mDatas.get(position);
        int deviceId = deviceInfo.getDeviceId();
        int type = TYPE_DEFAULT;
        switch (deviceId){
            //色温灯
            case DeviceList.DEVICE_ID_COLOR_TEMP1:
            case DeviceList.DEVICE_ID_COLOR_TEMP2:
                type = TYPE_LIGHT;
                break;
            //彩灯
            case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                type = TYPE_COLOR;
                break;
            //开关
            case DeviceList.DEVICE_ID_SOCKET:
            //窗帘
            case DeviceList.DEVICE_ID_CURTAIN :
            default:
                type = TYPE_DEFAULT;
                break;

        }
        return type;
    }


    //普通灯
    private  class ViewHolder {
        ImageView iv_arrow ,iv_picture;
        TextView name,time;
        ToggleButton  aSwitch,switchdelay;
        RelativeLayout relativeLayout;
    }

    //调色灯
    private  class ViewHolderColorLight {
        ImageView iv_picture;
        TextView name;
        ImageView iv_arrow,iv_red,iv_blue,iv_green,iv_orange,iv_slateblue,iv_palette;
        CircleView iv_color;
        ToggleButton aSwitch;
        LinearLayout brightness,colotlayout;
        SeekBar seekBar_light;

        private RelativeLayout layoutDelaytime;
        private ToggleButton switch2;
        private TextView tvDelaytime;



    }

    //调光灯
    private class ViewHolderDimmerLight{
        ImageView iv_picture;
        TextView name;
        ImageView iv_arrow;
        ToggleButton  aSwitch;
        LinearLayout layout, brightness,temperature;
        RelativeLayout layout_click;
        SeekBar seekBar_light,seekBar_temperature;
        private RelativeLayout layoutDelaytime;
        private ToggleButton switch2;
        private TextView tvDelaytime;


    }




}
