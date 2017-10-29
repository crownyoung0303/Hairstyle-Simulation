package com.simoncherry.arcamera.model;

/**
 * Created by Simon on 2017/7/19.
 */

public class Filter {
    private int Id;
    private int imgRes;
    private String name;

    public Filter(int id, int imgRes, String name) {
        Id = id;
        this.imgRes = imgRes;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
