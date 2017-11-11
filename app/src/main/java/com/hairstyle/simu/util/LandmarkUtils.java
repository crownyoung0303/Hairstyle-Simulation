package com.hairstyle.simu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.hairstyle.simu.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.DecimalFormat;


public class LandmarkUtils {

    private final static String TAG = LandmarkUtils.class.getSimpleName();


    public static String getDir(String dir) {
        File sdcard = Environment.getExternalStorageDirectory();
        return sdcard.getAbsolutePath() + File.separator + dir + File.separator;
    }

    public static void writeLandmarkToDisk(PointF[] landmarks, String path, String name) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            if (!isSuccess) {
                return;
            }
        }

        String jsonString = JSON.toJSONString(landmarks);
        Log.e(TAG, "landmarks: " + jsonString);

        String fileName = path + name + ".txt";
        try {
            int i = 0;
            FileWriter writer = new FileWriter(fileName);
            for (PointF point : landmarks) {
                int pointX = (int) point.x;
                int pointY = (int) point.y;
                String landmark = String.valueOf(pointX) + " " + String.valueOf(pointY) + "\n";
                Log.e(TAG, "write landmark[" + String.valueOf(i) + "]: " + landmark);
                i++;
                writer.write(landmark);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    public static String getMD5(String message) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] input = message.getBytes();

            byte[] buff = md.digest(input);

            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        //return md5str.toString().toUpperCase();
        return md5str.toString().toLowerCase();
    }

    private static byte [] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int [] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte [] yuv = new byte[inputWidth*inputHeight*3/2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        //scaled.recycle();
        return yuv;
    }

    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }

    public static boolean replaceTexture(Context context, STMobileMultiTrack106 tracker, String path) {
        Bitmap bitmap = BitmapUtils.getRequireWidthBitmap(path, 240);
        if (bitmap == null) {
            return false;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.e(TAG, "scale bitmap width: " + width);
            Log.e(TAG, "scale bitmap height: " + height);
            byte[] bytes = getNV21(width, height, bitmap);

            STMobileFaceAction[] faceActions = tracker.trackFaceAction(bytes, 0, width, height);
            if (faceActions == null || faceActions.length <= 0) {
                return false;
            } else {
                STMobileFaceAction faceAction = faceActions[0];
                PointF[] points = faceAction.getFace().getPointsArray();
                if (points == null || points.length < 106) {
                    return false;
                } else {
                    int size[] = BitmapUtils.getImageWidthHeight(path);
                    int imgWidth = size[0];
                    int imgHeight = size[1];
                    float scale = (float)imgWidth / width;

                    PointF[] tmp = new PointF[106];
                    PointF[] newPoints = new PointF[44];
                    for (int i=0; i<106; i++) {
                        PointF point = points[i];
                        Log.e(TAG, "point: " + point);
                        float x = point.x * scale / imgWidth;
                        float y = 1.0f - ((point.y * scale) / imgHeight);
                        tmp[i] = new PointF(x, y);
                        Log.e(TAG, "tmp: " + tmp[i]);
                    }

                    for (int i=0; i<44; i++) {
                        newPoints[i] = getRemapPoint(tmp, i);
                    }

                    String modelDir = getDir("/OpenGLDemo/txt/");
                    String baseMtlPath = modelDir + "base_face_uv3.mtl";
                    File mtlFile = new File(baseMtlPath);
                    if (!mtlFile.exists()) {
                        FileUtils.copyFileFromRawToOthers(context, R.raw.base_face_uv3_mtl, baseMtlPath);
                    }

                    String baseTexturePath = modelDir + "average_male.jpg";
                    File textureFile = new File(baseTexturePath);
                    if (!textureFile.exists()) {
                 //       FileUtils.copyFileFromRawToOthers(context, R.raw.average_male, baseTexturePath);
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream is = context.getResources().openRawResource(R.raw.base_face_uv3_obj);
                    try {
                        InputStreamReader reader = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(reader);
                        for(String str; (str = br.readLine()) != null; ) {
                            stringBuilder.append(str).append("\n");
                        }
                        br.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String obj_str = stringBuilder.toString();
                    Log.e(TAG, "read base_mask_obj: " + obj_str);

                    DecimalFormat decimalFormat = new DecimalFormat(".0000");
                    String[] ss = obj_str.split("\n");
                    int i = 48;
                    for (PointF point : newPoints) {
                        float x = point.x ;
                        float y = point.y;
                        String string = "vt " + decimalFormat.format(x) + " " + decimalFormat.format(y);
                        ss[i] = string;
                        i++;
                    }

                    String objPath = modelDir + "base_face_uv3_obj";
                    try {
                        FileWriter writer = new FileWriter(objPath);
                        for (String s : ss) {
                            writer.write(s + "\n");
                        }
                        writer.flush();
                        writer.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                        return false;
                    }
                }
            }
        }
    }

    private static PointF getRemapPoint(PointF[] tmp, int index) {
        index += 1;
        switch (index) {
            case 1:
                return tmp[0];
            case 2:
                return tmp[52];
            case 3:
                return tmp[34];
            case 4:
                return tmp[3];
            case 5:
                return tmp[8];
            case 6:
                return tmp[12];
            case 7:
                return tmp[84];
            case 8:
                return tmp[61];
            case 9:
                return tmp[90];
            case 10:
                return tmp[20];
            case 11:
                return tmp[24];
            case 12:
                return tmp[29];
            case 13:
                return tmp[32];
            case 14:
                return tmp[41];
            case 15:
                return tmp[39];
            case 16:
                return tmp[43];
            case 17:
                return tmp[58];
            case 18:
                return new PointF((tmp[36].x + tmp[39].x) * 0.5f, (tmp[36].y + tmp[39].y) * 0.5f);
            case 19:
                return tmp[36];
            case 20:
                return tmp[55];
            case 21:
                return tmp[82];
            case 22:
                return tmp[46];
            case 23:
                return tmp[83];
            case 24:
                return tmp[49];
            case 25:
                return tmp[53];
            case 26:
                return tmp[72];
            case 27:
                return tmp[54];
            case 28:
                return tmp[57];
            case 29:
                return tmp[73];
            case 30:
                return tmp[56];
            case 31:
                return tmp[59];
            case 32:
                return tmp[75];
            case 33:
                return tmp[60];
            case 34:
                return tmp[63];
            case 35:
                return tmp[76];
            case 36:
                return tmp[62];
            case 37:
                return tmp[97];
            case 38:
                return tmp[98];
            case 39:
                return tmp[99];
            case 40:
                return tmp[102];
            case 41:
                return tmp[103];
            case 42:
                return tmp[93];
            case 43:
                return tmp[101];
            case 44:
                return tmp[16];
            default:
                return tmp[0];
        }
    }
}
