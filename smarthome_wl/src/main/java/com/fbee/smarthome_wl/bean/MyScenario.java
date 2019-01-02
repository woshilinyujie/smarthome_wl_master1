package com.fbee.smarthome_wl.bean;

/**
 * 首页显示场景
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/24 9:24
 */
public class MyScenario {
    private int id ;
    private String name;
    private int imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
