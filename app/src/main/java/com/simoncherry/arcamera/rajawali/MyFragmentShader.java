package com.simoncherry.arcamera.rajawali;

import android.opengl.GLES20;
import android.support.annotation.RawRes;

import org.rajawali3d.materials.shaders.FragmentShader;
import org.rajawali3d.util.RawShaderLoader;

/**
 * Created by wecut-simon on 2017/9/2.
 */

public class MyFragmentShader extends FragmentShader {

    @RawRes
    private int mRawResId;

    private int muScreenWidthHandle;
    private int muScreenHeightHandle;
    private int muFlagHandle;

    private float mScreenW;
    private float mScreenH;
    private int mFlag = 0;


    public MyFragmentShader(int rawResId) {
        super();
        mNeedsBuild = false;
        mRawResId = rawResId;
        initialize();
    }

    @Override
    public void initialize() {
        mShaderString = RawShaderLoader.fetch(mRawResId);
    }

    @Override
    public void main() {

    }

    @Override
    public void setLocations(final int programHandle) {
        super.setLocations(programHandle);
        muScreenWidthHandle = getUniformLocation(programHandle, "uScreenW");
        muScreenHeightHandle = getUniformLocation(programHandle, "uScreenH");
        muFlagHandle = getUniformLocation(programHandle, "uFlag");
    }

    @Override
    public void applyParams() {
        super.applyParams();
        GLES20.glUniform1f(muScreenWidthHandle, mScreenW);
        GLES20.glUniform1f(muScreenHeightHandle, mScreenH);
        GLES20.glUniform1i(muFlagHandle, mFlag);
    }

    public void setScreenW(float width) {
        mScreenW = width;
    }

    public void setScreenH(float height) {
        mScreenH = height;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }
}
