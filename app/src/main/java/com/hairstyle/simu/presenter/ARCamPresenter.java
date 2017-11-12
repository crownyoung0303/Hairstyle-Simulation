package com.hairstyle.simu.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STUtils;
import com.hairstyle.simu.contract.ARCamContract;
import com.hairstyle.simu.model.DynamicPoint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;



public class ARCamPresenter implements ARCamContract.Presenter {
    private final static String TAG = ARCamPresenter.class.getSimpleName();

    private Context mContext;
    private ARCamContract.View mView;
    private List<DynamicPoint> mDynamicPoints;

    public ARCamPresenter(ARCamContract.View mView) {
        this.mView = mView;
        mDynamicPoints = new ArrayList<>();
    }

    @Override
    public void handlePhotoFrame(byte[] bytes, Bitmap mRajawaliBitmap, int photoWidth, int photoHeight) {
        Bitmap bitmap = Bitmap.createBitmap(photoWidth, photoHeight, Bitmap.Config.ARGB_8888);
        ByteBuffer b = ByteBuffer.wrap(bytes);
        bitmap.copyPixelsFromBuffer(b);
        if (mRajawaliBitmap != null) {
            Log.i(TAG, "mRajawaliBitmap != null");
            mRajawaliBitmap = Bitmap.createScaledBitmap(mRajawaliBitmap, photoWidth, photoHeight, false);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(mRajawaliBitmap, 0, 0, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            mRajawaliBitmap.recycle();
            mRajawaliBitmap = null;
        } else {
            Log.i(TAG, "mRajawaliBitmap == null");
        }
        savePhoto(bitmap);
        bitmap.recycle();
        bitmap = null;
    }

    @Override
    public void handleVideoFrame(byte[] bytes, int[] mRajawaliPixels) {
        if (mRajawaliPixels != null) {
            final ByteBuffer buf = ByteBuffer.allocate(mRajawaliPixels.length * 4)
                    .order(ByteOrder.LITTLE_ENDIAN);
            buf.asIntBuffer().put(mRajawaliPixels);
            mRajawaliPixels = null;
            byte[] tmpArray = buf.array();
            for (int i=0; i<bytes.length; i+=4) {
                byte a = tmpArray[i];
                byte r = tmpArray[i+1];
                byte g = tmpArray[i+2];
                byte b = tmpArray[i+3];
                // 取Rajawali不透明的部分
                boolean isBackground = r == 0 && g == 0 && b == 0 && a == 0;
                if (!isBackground) {
                    bytes[i] = a;
                    bytes[i + 1] = r;
                    bytes[i + 2] = g;
                    bytes[i + 3] = b;
                }
            }
        }
        mView.onGetVideoData(bytes);
    }

    @Override
    public void savePhoto(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenGLDemo/photo/";
        File folder = new File(path);
        if(!folder.exists() && !folder.mkdirs()){
            mView.onSavePhotoFailed();
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mView.onSavePhotoSuccess(jpegName);
    }

    @Override
    public void handle3dModelRotation(float pitch, float roll, float yaw) {
        mView.onGet3dModelRotation(-pitch, roll+90, -yaw);
    }

    @Override
    public void handle3dModelTransition(STMobileFaceAction[] faceActions,
                                        int orientation, int eye_dist, float yaw,
                                        int previewWidth, int previewHeight) {
        boolean rotate270 = orientation == 270;
        STMobileFaceAction r = faceActions[0];
        Rect rect;
        if (rotate270) {
            rect = STUtils.RotateDeg270(r.getFace().getRect(), previewWidth, previewHeight);
        } else {
            rect = STUtils.RotateDeg90(r.getFace().getRect(), previewWidth, previewHeight);
        }

        float centerX = (rect.right + rect.left) / 2.0f;
        float centerY = (rect.bottom + rect.top) / 2.0f;
        float x = (centerX / previewHeight) * 2.0f - 1.0f;
        float y = (centerY / previewWidth) * 2.0f - 1.0f;
        float tmp = eye_dist * 0.000001f - 1115;  // 1115xxxxxx ~ 1140xxxxxx - > 0 ~ 25
        tmp = (float) (tmp / Math.cos(Math.PI*yaw/180));
        tmp = tmp * 0.04f;  // 0 ~ 25 -> 0 ~ 1
        float z = tmp * 3.0f + 1.0f;
        Log.i(TAG, "transition: x= " + x + ", y= " + y + ", z= " + z);

        mView.onGet3dModelTransition(x, y, z);
    }

    @Override
    public void handleFaceLandmark(STMobileFaceAction[] faceActions, int orientation, int mouthAh,
                                   int previewWidth, int previewHeight) {
        boolean rotate270 = orientation == 270;
        if (faceActions != null && faceActions.length > 0) {
            STMobileFaceAction faceAction = faceActions[0];
            Log.i("Test", "-->> face count = "+faceActions.length);
            PointF[] points = faceAction.getFace().getPointsArray();
            float[] landmarkX = new float[points.length];
            float[] landmarkY = new float[points.length];
            for (int i = 0; i < points.length; i++) {
                if (rotate270) {
                    points[i] = STUtils.RotateDeg270(points[i], previewWidth, previewHeight);
                } else {
                    points[i] = STUtils.RotateDeg90(points[i], previewWidth, previewHeight);
                }

                landmarkX[i] = 1 - points[i].x / 480.0f;
                landmarkY[i] = points[i].y / 640.0f;
            }

            mView.onGetFaceLandmark(landmarkX, landmarkY, mouthAh);
        }
    }

    @Override
    public void handleChangeModel(float[] landmarkX, float[] landmarkY) {
        mDynamicPoints.clear();

        int length = landmarkX.length;
        for (int i=0; i<length; i++) {
            landmarkX[i] = (landmarkX[i] * 2f - 1f) * 7f;
            landmarkY[i] = ((1-landmarkY[i]) * 2f - 1f) * 9.3f;
        }

        mDynamicPoints.add(new DynamicPoint(29, landmarkX[41], landmarkY[41], 0.0f));
        mDynamicPoints.add(new DynamicPoint(15, landmarkX[39], landmarkY[39], 0.0f));
        mDynamicPoints.add(new DynamicPoint(20, (landmarkX[36] + landmarkX[39])*0.5f, (landmarkY[36] + landmarkY[39])*0.5f, 0.0f));
        mDynamicPoints.add(new DynamicPoint(10, landmarkX[36], landmarkY[36], 0.0f));
        mDynamicPoints.add(new DynamicPoint(30, landmarkX[34], landmarkY[34], 0.0f));

        mDynamicPoints.add(new DynamicPoint(21, landmarkX[32], landmarkY[32], 0.0f));
        mDynamicPoints.add(new DynamicPoint(22, landmarkX[29], landmarkY[29], 0.0f));
        mDynamicPoints.add(new DynamicPoint(23, landmarkX[24], landmarkY[24], 0.0f));
        mDynamicPoints.add(new DynamicPoint(24, landmarkX[20], landmarkY[20], 0.0f));

        mDynamicPoints.add(new DynamicPoint(9, landmarkX[16], landmarkY[16], 0.0f));

        mDynamicPoints.add(new DynamicPoint(28, landmarkX[0], landmarkY[0], 0.0f));
        mDynamicPoints.add(new DynamicPoint(27, landmarkX[3], landmarkY[3], 0.0f));
        mDynamicPoints.add(new DynamicPoint(26, landmarkX[8], landmarkY[8], 0.0f));
        mDynamicPoints.add(new DynamicPoint(25, landmarkX[12], landmarkY[12], 0.0f));

        mDynamicPoints.add(new DynamicPoint(19, landmarkX[61], landmarkY[61], 0.0f));
        mDynamicPoints.add(new DynamicPoint(32, landmarkX[60], landmarkY[60], 0.0f));
        mDynamicPoints.add(new DynamicPoint(16, landmarkX[75], landmarkY[75], 0.0f));
        mDynamicPoints.add(new DynamicPoint(31, landmarkX[59], landmarkY[59], 0.0f));
        mDynamicPoints.add(new DynamicPoint(17, landmarkX[58], landmarkY[58], 0.0f));
        mDynamicPoints.add(new DynamicPoint(33, landmarkX[63], landmarkY[63], 0.0f));
        mDynamicPoints.add(new DynamicPoint(18, landmarkX[76], landmarkY[76], 0.0f));
        mDynamicPoints.add(new DynamicPoint(34, landmarkX[62], landmarkY[62], 0.0f));

        mDynamicPoints.add(new DynamicPoint(14, landmarkX[52], landmarkY[52], 0.0f));
        mDynamicPoints.add(new DynamicPoint(36, landmarkX[53], landmarkY[53], 0.0f));
        mDynamicPoints.add(new DynamicPoint(11, landmarkX[72], landmarkY[72], 0.0f));
        mDynamicPoints.add(new DynamicPoint(35, landmarkX[54], landmarkY[54], 0.0f));
        mDynamicPoints.add(new DynamicPoint(12, landmarkX[55], landmarkY[55], 0.0f));
        mDynamicPoints.add(new DynamicPoint(38, landmarkX[56], landmarkY[56], 0.0f));
        mDynamicPoints.add(new DynamicPoint(13, landmarkX[73], landmarkY[73], 0.0f));
        mDynamicPoints.add(new DynamicPoint(37, landmarkX[57], landmarkY[57], 0.0f));

        mDynamicPoints.add(new DynamicPoint(0, landmarkX[49], landmarkY[49], 0.0f));
        mDynamicPoints.add(new DynamicPoint(1, landmarkX[82], landmarkY[82], 0.0f));
        mDynamicPoints.add(new DynamicPoint(2, landmarkX[83], landmarkY[83], 0.0f));
        mDynamicPoints.add(new DynamicPoint(3, landmarkX[46], landmarkY[46], 0.0f));
        mDynamicPoints.add(new DynamicPoint(4, landmarkX[43], landmarkY[43], 0.0f));

        mDynamicPoints.add(new DynamicPoint(5, landmarkX[90], landmarkY[90], 0.0f));
        mDynamicPoints.add(new DynamicPoint(41, landmarkX[99], landmarkY[99], 0.0f));
        mDynamicPoints.add(new DynamicPoint(42, landmarkX[98], landmarkY[98], 0.0f));
        mDynamicPoints.add(new DynamicPoint(43, landmarkX[97], landmarkY[97], 0.0f));
        mDynamicPoints.add(new DynamicPoint(6, landmarkX[84], landmarkY[84], 0.0f));
        mDynamicPoints.add(new DynamicPoint(40, landmarkX[103], landmarkY[103], 0.0f));
        mDynamicPoints.add(new DynamicPoint(7, landmarkX[102], landmarkY[102], 0.0f));
        mDynamicPoints.add(new DynamicPoint(39, landmarkX[101], landmarkY[101], 0.0f));
        mDynamicPoints.add(new DynamicPoint(8, landmarkX[93], landmarkY[93], 0.0f));

        mView.onGetChangePoint(mDynamicPoints);
    }
}
