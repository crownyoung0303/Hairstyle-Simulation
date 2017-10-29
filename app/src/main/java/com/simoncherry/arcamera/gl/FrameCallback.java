package com.simoncherry.arcamera.gl;

/**
 * Created by Simon on 2017/7/5.
 */

public interface FrameCallback {
    void onFrame(byte[] bytes, long time);
}
