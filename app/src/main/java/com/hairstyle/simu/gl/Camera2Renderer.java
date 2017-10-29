package com.hairstyle.simu.gl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Renderer implements MyRenderer {

    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private TextureController mController;
    private int cameraId;

    private HandlerThread mThread;
    private Handler mHandler;
    private Size mPreviewSize;


    public Camera2Renderer(CameraManager cameraManager, TextureController mController, int cameraId) {
        this.mCameraManager = cameraManager;
        this.mController = mController;
        this.cameraId = cameraId;

        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    public void onDestroy() {
        if(mDevice != null){
            mDevice.close();
            mDevice = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            if(mDevice != null){
                mDevice.close();
                mDevice = null;
            }

            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
            StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            //自定义规则，选个大小
            mPreviewSize = sizes[0];
            mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());

            mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mDevice = camera;
                    try {
                        Surface surface = new Surface(mController.getTexture());
                        final CaptureRequest.Builder builder = mDevice.createCaptureRequest(TEMPLATE_PREVIEW);
                        builder.addTarget(surface);
                        mController.getTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

                        mDevice.createCaptureSession(Arrays.asList(surface), new
                                CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        try {
                                            session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureProgressed(
                                                        @NonNull CameraCaptureSession session,
                                                        @NonNull CaptureRequest request,
                                                        @NonNull CaptureResult partialResult) {
                                                    super.onCaptureProgressed(session, request, partialResult);
                                                }

                                                @Override
                                                public void onCaptureCompleted(
                                                        @NonNull CameraCaptureSession session,
                                                        @NonNull CaptureRequest request,
                                                        @NonNull TotalCaptureResult result) {
                                                    super.onCaptureCompleted(session, request, result);
                                                    mController.requestRender();
                                                }
                                            },mHandler);
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                    }

                                }, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    mDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                }

            }, mHandler);
        } catch (SecurityException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }
}
