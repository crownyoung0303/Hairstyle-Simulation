package com.hairstyle.simu.contract;

import android.graphics.Bitmap;

import com.sensetime.stmobileapi.STMobileFaceAction;
import com.hairstyle.simu.model.DynamicPoint;

import java.util.List;



public interface ARCamContract {
    interface View {
        void onSavePhotoSuccess(String fileName);
        void onSavePhotoFailed();
        void onGetVideoData(byte[] bytes);
        void onGet3dModelRotation(float pitch, float roll, float yaw);
        void onGet3dModelTransition(float x, float y, float z);
        void onGetFaceLandmark(float[] landmarkX, float[] landmarkY, int isMouthOpen);
        void onGetChangePoint(List<DynamicPoint> mDynamicPoints);
    }

    interface Presenter {
        void handlePhotoFrame(byte[] bytes, Bitmap mRajawaliBitmap, int photoWidth, int photoHeight);
        void handleVideoFrame(byte[] bytes, int[] mRajawaliPixels);
        void savePhoto(Bitmap bitmap);
        void handle3dModelRotation(float pitch, float roll, float yaw);
        void handle3dModelTransition(STMobileFaceAction[] faceActions,
                                     int orientation, int eye_dist, float yaw,
                                     int previewWidth, int previewHeight);
        void handleFaceLandmark(STMobileFaceAction[] faceActions, int orientation, int mouthAh,
                                int previewWidth, int previewHeight);
        void handleChangeModel(float[] landmarkX, float[] landmarkY);
    }
}
