package com.fbee.vlcutil;

import android.content.Context;


public class MyContext {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }
}
