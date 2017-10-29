package com.simoncherry.arcamera.rajawali;

import android.support.annotation.RawRes;

import org.rajawali3d.materials.shaders.VertexShader;
import org.rajawali3d.util.RawShaderLoader;

/**
 * Created by wecut-simon on 2017/9/2.
 */

public class MyVertexShader extends VertexShader {

    @RawRes
    private int mRawResId;

    public MyVertexShader(int rawResId) {
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
    }

    @Override
    public void applyParams() {
        super.applyParams();
    }
}
