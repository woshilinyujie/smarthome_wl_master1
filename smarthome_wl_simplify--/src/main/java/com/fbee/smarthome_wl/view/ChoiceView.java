package com.fbee.smarthome_wl.view;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

/**
 * Created by wl on 2017/4/6.
 */

public class ChoiceView extends FrameLayout implements Checkable {
    private TextView mTextView;
    private Checkable mchecked;

    public ChoiceView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_eques_alarm_time, this);
        mTextView = (TextView) findViewById(R.id.title);
        mchecked = (Checkable) findViewById(R.id.checkedView);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    @Override
    public void setChecked(boolean checked) {
        mchecked.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mchecked.isChecked();
    }

    @Override
    public void toggle() {
        mchecked.toggle();
    }
}
