package com.fbee.vlcutil.utils;


import com.fbee.vlcutil.MyContext;

import java.io.File;


public class ImageUtils {

    public static File getNewImageFile() {
        File dir = MyContext.getContext().getExternalFilesDir("image");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        String name = MD5Utils.getMD5(String.format("Image.%d", System.currentTimeMillis()));
        File file = new File(dir, name + ".jpg");
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        return file;
    }
}
