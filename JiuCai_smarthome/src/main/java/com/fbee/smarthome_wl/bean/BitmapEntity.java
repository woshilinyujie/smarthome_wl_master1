package com.fbee.smarthome_wl.bean;

import android.graphics.Bitmap;

/**
 * Created by wl on 2017/1/20.
 */

public class BitmapEntity {

    private String filePath;
    private Bitmap bitmap;
    private String filename;
    private String videosize;

    public BitmapEntity(Bitmap bitmap, String filename, String videosize,String filePath) {
        this.filePath = filePath;
        this.bitmap = bitmap;
        this.filename = filename;
        this.videosize = videosize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVideosize() {
        return videosize;
    }

    public void setVideosize(String videosize) {
        this.videosize = videosize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
