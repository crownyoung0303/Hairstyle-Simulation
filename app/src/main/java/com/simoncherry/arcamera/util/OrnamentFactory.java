package com.simoncherry.arcamera.util;

import android.graphics.Color;

import com.simoncherry.arcamera.R;
import com.simoncherry.arcamera.gl.TextureController;
import com.simoncherry.arcamera.model.Ornament;
import com.simoncherry.arcamera.rajawali.CustomMaterialPlugin;
import com.simoncherry.arcamera.rajawali.CustomVertexShaderMaterialPlugin;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.plugins.IMaterialPlugin;
import org.rajawali3d.primitives.NPrism;
import org.rajawali3d.primitives.Sphere;

import java.util.ArrayList;
import java.util.List;



public class OrnamentFactory {
    public final static int NO_COLOR = 2333;

    public static List<Ornament> getPresetOrnament() {
        List<Ornament> ornaments = new ArrayList<>();
        ornaments.add(getNoOrnament());

        ornaments.add(getGlasses());

        return ornaments;
    }

    private static Ornament getNoOrnament() {
        Ornament ornament = new Ornament();
        ornament.setType(Ornament.MODEL_TYPE_NONE);
        ornament.setImgResId(R.drawable.ic_remove);
        return ornament;
    }


    private static Ornament getGlasses() {
        Ornament ornament = new Ornament();

        Ornament.Model model = new Ornament.Model();
        model.setModelResId(R.raw.glasses_obj);
        model.setScale(0.15f);
        model.setOffset(0.01f, 0.05f, -0.1f);
        model.setRotate(0, 0, 0);

        model.setNeedStreaming(true);
        model.setStreamingViewWidth(800);
        model.setStreamingViewHeight(800);
        model.setStreamingModelWidth(6);
        model.setStreamingModelHeight(6);
        model.setStreamingScale(1.0f);
        model.setStreamingOffsetX(0.01f);
        model.setStreamingOffsetY(0.17f);
        model.setStreamingOffsetZ(0.40f);
        model.setAlphaMapResId(R.drawable.glasses_alpha);
        model.setStreamingViewType(Ornament.Model.STREAMING_WEB_VIEW);
        model.setStreamingViewMirror(true);
        model.setStreamingModelTransparent(true);
        model.setStreamingTextureInfluence(0.6f);
        model.setColorInfluence(0.4f);

        ornament.setType(Ornament.MODEL_TYPE_STATIC);
        ornament.setImgResId(R.drawable.ic_glasses);
        List<Ornament.Model> modelList = new ArrayList<>();
        modelList.add(model);
        ornament.setModelList(modelList);
        ornament.setFrameCallbackType(TextureController.FRAME_CALLBACK_DISABLE);
        ornament.setToastMsg("镜面显示浏览器画面");

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
