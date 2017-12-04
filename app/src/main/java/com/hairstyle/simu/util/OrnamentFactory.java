package com.hairstyle.simu.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import com.hairstyle.simu.R;
import com.hairstyle.simu.gl.Camera1Renderer;
import com.hairstyle.simu.gl.CameraTrackRenderer;
import com.hairstyle.simu.gl.MyRenderer;
import com.hairstyle.simu.gl.TextureController;
import com.hairstyle.simu.model.Ornament;
import com.sensetime.stmobileapi.STMobileFaceAction;

import java.util.ArrayList;
import java.util.List;



public class OrnamentFactory {
    public final static int NO_COLOR = 2333;

    public static List<Ornament> getPresetOrnament(int i) {
        List<Ornament> ornaments = new ArrayList<>();
        ornaments.add(getNoOrnament());

        ornaments.add(getrec(i));
        ornaments.add(gethair1brown());
        ornaments.add(gethair1darkblue());
        ornaments.add(gethair1pink());
        ornaments.add(gethair1blue());
        ornaments.add(gethair1red());
        ornaments.add(gethair1green());
        ornaments.add(gethair1grey());
        ornaments.add(gethair2());
        ornaments.add(gethair3());



        return ornaments;
    }

    private static Ornament getNoOrnament() {
        Ornament ornament = new Ornament();
        ornament.setType(Ornament.MODEL_TYPE_NONE);
        ornament.setImgResId(R.drawable.ic_remove);
        return ornament;
    }

    private static Ornament getrec(int i) {

        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();
        switch(i){
            case 1: model.setModelResId(R.raw.hair3_obj);break;
            case 2: model.setModelResId(R.raw.hair1darkblue_obj);break;
            case 3: model.setModelResId(R.raw.hair1green_obj);break;
            case 4: model.setModelResId(R.raw.hair1brown_obj);break;
            case 5: model.setModelResId(R.raw.hair1pink_obj);break;
            case 6: model.setModelResId(R.raw.hair1grey_obj);break;
            case 7: model.setModelResId(R.raw.hair1blue_obj);break;
            case 8: model.setModelResId(R.raw.hair1red_obj);break;
            case 9: model.setModelResId(R.raw.hair2_obj);break;
        }
        model.setScale(0.2f);
        model.setOffset(0.01f, 0.05f, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_rec);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }

    private static Ornament gethair1brown() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1brown_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1brown);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1darkblue() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1darkblue_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1darkblue);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1pink() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1pink_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1pink);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1blue() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1blue_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1blue);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1green() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1green_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1green);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1grey() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1grey_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1grey);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair1red() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1red_obj);
        model.setScale(0.23f);
        model.setOffset(-0.1f, 0, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1red);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }

    private static Ornament gethair2() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair2_obj);
        model.setScale(0.2f);
        model.setOffset(0, 0.05f, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair2);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }
    private static Ornament gethair3() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair3_obj);
        model.setScale(0.2f);
        model.setOffset(0, 0.05f, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair2);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);

        return ornament;
    }






    public static Ornament getMask(String texturePath) {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();
        model.setModelResId(-1);
        model.setTexturePath(texturePath);
        model.setScale(0.25f);
        model.setOffset(0, 0, 0);
        model.setRotate(0, 0, 0);
        model.setColor(NO_COLOR);
        model.setNeedSkinColor(true);

        ornament.setType(Ornament.MODEL_TYPE_DYNAMIC);
        ornament.setImgResId(0);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        return ornament;
    }
}
