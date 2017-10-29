package com.hairstyle.simu.gl;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Camera1Renderer implements MyRenderer {
    private Camera mCamera;
    private TextureController mController;
    private int cameraId;

    public Camera1Renderer(TextureController mController, int cameraId) {
        this.mController = mController;
        this.cameraId = cameraId;
    }

    @Override
    public void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera = Camera.open(cameraId);
        mController.setImageDirection(cameraId);
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        mController.setDataSize(size.height, size.width);
        try {
            mCamera.setPreviewTexture(mController.getTexture());
            mController.getTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mController.requestRender();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }
}
