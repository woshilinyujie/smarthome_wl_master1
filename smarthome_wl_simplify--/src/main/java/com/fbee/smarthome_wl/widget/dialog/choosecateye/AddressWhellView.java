package com.fbee.smarthome_wl.widget.dialog.choosecateye;

import android.content.Context;
import android.util.AttributeSet;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.widget.wheel.WheelView;



public class AddressWhellView extends WheelView {

	/**
	 * Constructor
	 */
	public AddressWhellView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.wheelBackground = R.drawable.address_wheel_bg;
		this.wheelForeground = R.drawable.address_wheel_val;
	}

	/**
	 * Constructor
	 */
	public AddressWhellView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.wheelBackground = R.drawable.address_wheel_bg;
		this.wheelForeground = R.drawable.address_wheel_val;
	}

	/**
	 * Constructor
	 */
	public AddressWhellView(Context context) {
		super(context);
		this.wheelBackground = R.drawable.address_wheel_bg;
		this.wheelForeground = R.drawable.address_wheel_val;
	}
}
