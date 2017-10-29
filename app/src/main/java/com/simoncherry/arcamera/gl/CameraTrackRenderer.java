package com.simoncherry.arcamera.gl;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.simoncherry.arcamera.util.Accelerometer;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;

/**
 * Created by Simon on 2017/7/5.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraTrackRenderer implements MyRenderer {

    private final static String TAG = CameraTrackRenderer.class.getSimpleName();

    ///< 检测脸部动作：张嘴、眨眼、抬眉、点头、摇头
    private static final int ST_MOBILE_TRACKING_ENABLE_FACE_ACTION = 0x00000020;
    private static final int ST_MOBILE_FACE_DETECT   =  0x00000001;    ///<  人脸检测
    private static final int ST_MOBILE_EYE_BLINK     =  0x00000002;  ///<  眨眼
    private static final int ST_MOBILE_MOUTH_AH      =  0x00000004;    ///<  嘴巴大张
    private static final int ST_MOBILE_HEAD_YAW      =  0x00000008;    ///<  摇头
    private static final int ST_MOBILE_HEAD_PITCH    =  0x00000010;    ///<  点头
    private static final int ST_MOBILE_BROW_JUMP     =  0x00000020;    ///<  眉毛挑动

    private final int PREVIEW_WIDTH = 640;
    private final int PREVIEW_HEIGHT = 480;

    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private TextureController mController;
    private ImageReader imageReader;
    private int cameraId;

    private HandlerThread mThread;
    private Handler mHandler;
    private Size mPreviewSize;

    private STMobileMultiTrack106 tracker;
    private byte nv21[];
    private HandlerThread mInferenceThread;
    private Handler mInferenceHandler;


    public CameraTrackRenderer(Context context, CameraManager cameraManager, TextureController mController, int cameraId) {
        this.mCameraManager = cameraManager;
        this.mController = mController;
        this.cameraId = cameraId;

        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        mInferenceThread = new HandlerThread("InferenceThread");
        mInferenceThread.start();
        mInferenceHandler = new Handler(mInferenceThread.getLooper());

        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];

        tracker = new STMobileMultiTrack106(context, ST_MOBILE_TRACKING_ENABLE_FACE_ACTION);
        int max = 1;
        tracker.setMaxDetectableFaces(max);
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
            assert map != null;
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            //自定义规则，选个大小
            mPreviewSize = sizes[0];
            mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());

            imageReader = ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 2);
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    handlePreviewData(reader);
                }
            }, mHandler);

            mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mDevice = camera;
                    try {
                        Surface surface = new Surface(mController.getTexture());
                        final CaptureRequest.Builder builder = mDevice.createCaptureRequest(TEMPLATE_PREVIEW);
                        builder.addTarget(surface);
                        builder.addTarget(imageReader.getSurface());
                        mController.getTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

                        mDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), new
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

    private void handlePreviewData(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Log.i(TAG, "get image bytes size: " + bytes.length);

            System.arraycopy(bytes, 0, nv21, 0, bytes.length);

            mInferenceHandler.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        long startTime = System.currentTimeMillis();

                        CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(String.valueOf(cameraId));
                        int orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        boolean frontCamera = (cameraId == 1);
                        int direction = Accelerometer.getDirection();

                        if (frontCamera &&
                                ((orientation == 270 && (direction & 1) == 1) ||
                                        (orientation == 90 && (direction & 1) == 0))) {
                            direction = (direction ^ 2);
                        }

                        STMobileFaceAction[] faceActions = tracker.trackFaceAction(nv21, direction, PREVIEW_WIDTH, PREVIEW_HEIGHT);

                        long endTime = System.currentTimeMillis();
                        float trackTime = endTime - startTime;
                        Log.i(TAG, "start time: " + startTime);
                        Log.i(TAG, "end time: " + endTime);
                        Log.i(TAG, "track time: " + trackTime);

                        if (faceActions != null && faceActions.length > 0) {
                            Log.i(TAG, "-->> faceActions: faceActions[0].face=" + faceActions[0].face.rect.toString() + ", " +
                                    "pitch = " + faceActions[0].face.pitch + ", " +
                                    "roll=" + faceActions[0].face.roll + ", " +
                                    "yaw=" + faceActions[0].face.yaw + ", " +
                                    "face_action = " + faceActions[0].face_action + ", " +
                                    "face_count = " + faceActions.length);
                            if (trackCallBackListener != null) {
                                trackCallBackListener.onTrackDetected(faceActions, orientation, (int) trackTime,
                                        faceActions[0].face.pitch,
                                        faceActions[0].face.roll,
                                        faceActions[0].face.yaw,
                                        faceActions[0].face.eye_dist,
                                        faceActions[0].face.ID,
                                        checkFlag(faceActions[0].face_action, ST_MOBILE_EYE_BLINK),
                                        checkFlag(faceActions[0].face_action, ST_MOBILE_MOUTH_AH),
                                        checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_YAW),
                                        checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_PITCH),
                                        checkFlag(faceActions[0].face_action, ST_MOBILE_BROW_JUMP));
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    private int checkFlag(int action, int flag) {
        int res = action & flag;
        return res == 0 ? 0 : 1;
    }

    public interface TrackCallBackListener {
        void onTrackDetected(STMobileFaceAction[] faceActions, int orientation,
                             int value, float pitch, float roll, float yaw, int eye_dist,
                             int id, int eyeBlink, int mouthAh, int headYaw, int headPitch, int browJump);
    }

    private TrackCallBackListener trackCallBackListener;

    public void setTrackCallBackListener(TrackCallBackListener trackCallBackListener) {
        this.trackCallBackListener = trackCallBackListener;
    }
}
