package com.hairstyle.simu.rajawali;

import android.support.annotation.RawRes;

import org.rajawali3d.materials.shaders.VertexShader;
import org.rajawali3d.util.RawShaderLoader;



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
