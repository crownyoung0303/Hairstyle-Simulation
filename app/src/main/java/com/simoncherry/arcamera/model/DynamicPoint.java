package com.simoncherry.arcamera.model;

/**
 * Created by Simon on 2017/7/18.
 */

public class DynamicPoint {
    private int index;
    private float x;
    private float y;
    private float z;

    public DynamicPoint(int index, float x, float y, float z) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "DynamicPoint{" +
                "index=" + index +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
