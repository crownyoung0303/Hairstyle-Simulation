package com.hairstyle.simu.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.RawRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Simon on 2017/7/19.
 */

public class FileUtils {

    public static String getBaseFolder(Context context){
        String baseFolder = Environment.getExternalStorageDirectory() + "/OpenGLDemo/";
        File f = new File(baseFolder);
        if(!f.exists()){
            boolean b = f.mkdirs();
            if(!b){
                baseFolder = context.getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }

    public static String getPath(Context context, String path,String fileName){
        String p = getBaseFolder(context) + path;
        File f = new File(p);
        if(!f.exists() && !f.mkdirs()){
            return getBaseFolder(context) + fileName;
        }
        return p + fileName;
    }

    public static void copyFileFromRawToOthers(final Context context, @RawRes int id, final String targetPath) {
        InputStream in = context.getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetPath);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
