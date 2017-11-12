package com.hairstyle.simu.util;

import android.graphics.Color;

import com.hairstyle.simu.R;
import com.hairstyle.simu.gl.TextureController;
import com.hairstyle.simu.model.Ornament;

import java.util.ArrayList;
import java.util.List;



public class OrnamentFactory {
    public final static int NO_COLOR = 2333;

    public static List<Ornament> getPresetOrnament() {
        List<Ornament> ornaments = new ArrayList<>();
        ornaments.add(getNoOrnament());

        ornaments.add(gethair1());
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


    private static Ornament gethair1() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();

        model.setModelResId(R.raw.hair1_obj);
        model.setScale(0.2f);
        model.setOffset(0.01f, 0.05f, -1.0f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_hair1);
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
        model.setOffset(0.01f, 0.05f, -1.0f);
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

        model.setModelResId(R.raw.hair1_obj);
        model.setScale(0.2f);
        model.setOffset(0.01f, 0.05f, -1.0f);
        model.setRotate(0, 0, 0);
        model.setColor(Color.RED);

        model.setNeedStreaming(true);


        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_red);
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
