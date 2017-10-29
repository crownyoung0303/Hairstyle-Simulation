package com.hairstyle.simu.model;

import com.hairstyle.simu.R;
import com.hairstyle.simu.gl.TextureController;
import com.hairstyle.simu.util.OrnamentFactory;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.materials.plugins.IMaterialPlugin;

import java.util.List;



public class Ornament {

    public static final int MODEL_TYPE_NONE = -1;
    public static final int MODEL_TYPE_STATIC = 0;
    public static final int MODEL_TYPE_DYNAMIC = 1;
    public static final int MODEL_TYPE_SHADER = 2;

    private int type = MODEL_TYPE_NONE;
    private int imgResId;
    private List<Model> modelList;
    private List<Object3D> object3DList;
    private List<List<IMaterialPlugin>> materialList;
    private float timeStep;
    private float timePeriod;
    private float scale;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float rotateX;
    private float rotateY;
    private float rotateZ;
    private boolean enableRotation = true;
    private boolean enableTransition = true;
    private boolean enableScale = true;
    private String toastMsg = null;

    private boolean hasShaderPlane;
    private int vertResId;
    private int fragResId;
    private float planeOffsetX;
    private float planeOffsetY;
    private float planeOffsetZ;

    private int frameCallbackType = TextureController.FRAME_CALLBACK_DEFAULT;
    private int selectFilterId = R.id.menu_camera_default;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public List<Model> getModelList() {
        return modelList;
    }

    public void setModelList(List<Model> modelList) {
        this.modelList = modelList;
    }

    public List<Object3D> getObject3DList() {
        return object3DList;
    }

    public void setObject3DList(List<Object3D> mObject3DList) {
        this.object3DList = mObject3DList;
    }

    public List<List<IMaterialPlugin>> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<List<IMaterialPlugin>> materialList) {
        this.materialList = materialList;
    }

    public float getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(float timeStep) {
        this.timeStep = timeStep;
    }

    public float getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(float timePeriod) {
        this.timePeriod = timePeriod;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(float offsetZ) {
        this.offsetZ = offsetZ;
    }

    public float getRotateX() {
        return rotateX;
    }

    public void setRotateX(float rotateX) {
        this.rotateX = rotateX;
    }

    public float getRotateY() {
        return rotateY;
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public void setRotateZ(float rotateZ) {
        this.rotateZ = rotateZ;
    }

    public boolean isEnableRotation() {
        return enableRotation;
    }

    public void setEnableRotation(boolean enableRotation) {
        this.enableRotation = enableRotation;
    }

    public boolean isEnableTransition() {
        return enableTransition;
    }

    public void setEnableTransition(boolean enableTransition) {
        this.enableTransition = enableTransition;
    }

    public boolean isEnableScale() {
        return enableScale;
    }

    public void setEnableScale(boolean enableScale) {
        this.enableScale = enableScale;
    }

    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }

    public boolean isHasShaderPlane() {
        return hasShaderPlane;
    }

    public void setHasShaderPlane(boolean hasShaderPlane) {
        this.hasShaderPlane = hasShaderPlane;
    }

    public int getVertResId() {
        return vertResId;
    }

    public void setVertResId(int vertResId) {
        this.vertResId = vertResId;
    }

    public int getFragResId() {
        return fragResId;
    }

    public void setFragResId(int fragResId) {
        this.fragResId = fragResId;
    }

    public float getPlaneOffsetX() {
        return planeOffsetX;
    }

    public void setPlaneOffsetX(float planeOffsetX) {
        this.planeOffsetX = planeOffsetX;
    }

    public float getPlaneOffsetY() {
        return planeOffsetY;
    }

    public void setPlaneOffsetY(float planeOffsetY) {
        this.planeOffsetY = planeOffsetY;
    }

    public float getPlaneOffsetZ() {
        return planeOffsetZ;
    }

    public void setPlaneOffsetZ(float planeOffsetZ) {
        this.planeOffsetZ = planeOffsetZ;
    }

    public int getFrameCallbackType() {
        return frameCallbackType;
    }

    public void setFrameCallbackType(int frameCallbackType) {
        this.frameCallbackType = frameCallbackType;
    }

    public int getSelectFilterId() {
        return selectFilterId;
    }

    public void setSelectFilterId(int selectFilterId) {
        this.selectFilterId = selectFilterId;
    }

    public static class Model {

        public static final int STREAMING_IMAGE_VIEW = 0;
        public static final int STREAMING_WEB_VIEW = 1;

        public static final int STREAMING_PLANE_MODEL = 0;
        public static final int STREAMING_SPHERE_MODEL = 1;

        public static final int BUILD_IN_PLANE = 0;
        public static final int BUILD_IN_CUBE = 1;
        public static final int BUILD_IN_SPHERE = 2;

        private String name;
        private int modelResId = -1;
        private float scale;
        private float offsetX;
        private float offsetY;
        private float offsetZ;
        private float rotateX;
        private float rotateY;
        private float rotateZ;
        private int color = OrnamentFactory.NO_COLOR;
        private List<Animation3D> animation3Ds;
        private int textureResId = -1;
        private String texturePath = null;
        private boolean isDynamic;
        private boolean needSkinColor;
        private boolean needObjectPick;
        // for object pick
        private boolean isPicked;
        private float beforeX;
        private float beforeY;
        private float beforeZ;
        private float afterX;
        private float afterY;
        private float afterZ;
        private float beforeAngle;
        private float afterAngle;
        private int axisX;
        private int axisY;
        private int axisZ;
        // for StreamingTexture
        private boolean needStreaming;
        private int streamingViewType = STREAMING_IMAGE_VIEW;
        private int streamingViewWidth;
        private int streamingViewHeight;
        private int streamingModelType = STREAMING_PLANE_MODEL;
        private float streamingModelWidth;
        private float streamingModelHeight;
        private int streamingModelSegmentsW = 1;
        private int streamingModelSegmentsH = 1;
        private float streamingScale;
        private float streamingOffsetX;
        private float streamingOffsetY;
        private float streamingOffsetZ;
        private float streamingRotateX;
        private float streamingRotateY;
        private float streamingRotateZ;
        private boolean streamingViewMirror = false;
        private boolean streamingModelTransparent = false;
        private float streamingTextureInfluence = 1.0f;
        private float ColorInfluence = 0;
        private int alphaMapResId = -1;
        // for build-in
        private int buildInType = -1;
        private float buildInWidth;
        private float buildInHeight;
        private int buildInSegmentsW;
        private int buildInSegmentsH;
        private int materialId = -1;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getModelResId() {
            return modelResId;
        }

        public void setModelResId(int modelResId) {
            this.modelResId = modelResId;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public float getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }

        public float getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }

        public float getOffsetZ() {
            return offsetZ;
        }

        public void setOffsetZ(float offsetZ) {
            this.offsetZ = offsetZ;
        }

        public float getRotateX() {
            return rotateX;
        }

        public void setRotateX(float rotateX) {
            this.rotateX = rotateX;
        }

        public float getRotateY() {
            return rotateY;
        }

        public void setRotateY(float rotateY) {
            this.rotateY = rotateY;
        }

        public float getRotateZ() {
            return rotateZ;
        }

        public void setRotateZ(float rotateZ) {
            this.rotateZ = rotateZ;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public List<Animation3D> getAnimation3Ds() {
            return animation3Ds;
        }

        public void setAnimation3Ds(List<Animation3D> animation3Ds) {
            this.animation3Ds = animation3Ds;
        }

        public int getTextureResId() {
            return textureResId;
        }

        public void setTextureResId(int textureResId) {
            this.textureResId = textureResId;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public void setTexturePath(String texturePath) {
            this.texturePath = texturePath;
        }

        public boolean isDynamic() {
            return isDynamic;
        }

        public void setDynamic(boolean isDynamic) {
            this.isDynamic = isDynamic;
        }

        public boolean isNeedSkinColor() {
            return needSkinColor;
        }

        public void setNeedSkinColor(boolean needSkinColor) {
            this.needSkinColor = needSkinColor;
        }

        public boolean isNeedObjectPick() {
            return needObjectPick;
        }

        public void setNeedObjectPick(boolean needObjectPick) {
            this.needObjectPick = needObjectPick;
        }

        public void setOffset(float x, float y, float z) {
            setOffsetX(x);
            setOffsetY(y);
            setOffsetZ(z);
        }

        public void setRotate(float x, float y, float z) {
            setRotateX(x);
            setRotateY(y);
            setRotateZ(z);
        }

        public boolean isPicked() {
            return isPicked;
        }

        public void setPicked(boolean picked) {
            isPicked = picked;
        }

        public float getBeforeX() {
            return beforeX;
        }

        public void setBeforeX(float beforeX) {
            this.beforeX = beforeX;
        }

        public float getBeforeY() {
            return beforeY;
        }

        public void setBeforeY(float beforeY) {
            this.beforeY = beforeY;
        }

        public float getBeforeZ() {
            return beforeZ;
        }

        public void setBeforeZ(float beforeZ) {
            this.beforeZ = beforeZ;
        }

        public float getAfterX() {
            return afterX;
        }

        public void setAfterX(float afterX) {
            this.afterX = afterX;
        }

        public float getAfterY() {
            return afterY;
        }

        public void setAfterY(float afterY) {
            this.afterY = afterY;
        }

        public float getAfterZ() {
            return afterZ;
        }

        public void setAfterZ(float afterZ) {
            this.afterZ = afterZ;
        }

        public float getBeforeAngle() {
            return beforeAngle;
        }

        public void setBeforeAngle(float beforeAngle) {
            this.beforeAngle = beforeAngle;
        }

        public float getAfterAngle() {
            return afterAngle;
        }

        public void setAfterAngle(float afterAngle) {
            this.afterAngle = afterAngle;
        }

        public int getAxisX() {
            return axisX;
        }

        public void setAxisX(int axisX) {
            this.axisX = axisX;
        }

        public int getAxisY() {
            return axisY;
        }

        public void setAxisY(int axisY) {
            this.axisY = axisY;
        }

        public int getAxisZ() {
            return axisZ;
        }

        public void setAxisZ(int axisZ) {
            this.axisZ = axisZ;
        }

        public boolean isNeedStreaming() {
            return needStreaming;
        }

        public void setNeedStreaming(boolean needStreaming) {
            this.needStreaming = needStreaming;
        }

        public int getStreamingViewType() {
            return streamingViewType;
        }

        public void setStreamingViewType(int streamingViewType) {
            this.streamingViewType = streamingViewType;
        }

        public int getStreamingViewWidth() {
            return streamingViewWidth;
        }

        public void setStreamingViewWidth(int streamingViewWidth) {
            this.streamingViewWidth = streamingViewWidth;
        }

        public int getStreamingViewHeight() {
            return streamingViewHeight;
        }

        public void setStreamingViewHeight(int streamingViewHeight) {
            this.streamingViewHeight = streamingViewHeight;
        }

        public int getStreamingModelType() {
            return streamingModelType;
        }

        public void setStreamingModelType(int streamingModelType) {
            this.streamingModelType = streamingModelType;
        }

        public float getStreamingModelWidth() {
            return streamingModelWidth;
        }

        public void setStreamingModelWidth(float streamingModelWidth) {
            this.streamingModelWidth = streamingModelWidth;
        }

        public float getStreamingModelHeight() {
            return streamingModelHeight;
        }

        public void setStreamingModelHeight(float streamingModelHeight) {
            this.streamingModelHeight = streamingModelHeight;
        }

        public int getStreamingModelSegmentsW() {
            return streamingModelSegmentsW;
        }

        public void setStreamingModelSegmentsW(int streamingModelSegmentsW) {
            this.streamingModelSegmentsW = streamingModelSegmentsW;
        }

        public int getStreamingModelSegmentsH() {
            return streamingModelSegmentsH;
        }

        public void setStreamingModelSegmentsH(int streamingModelSegmentsH) {
            this.streamingModelSegmentsH = streamingModelSegmentsH;
        }

        public float getStreamingScale() {
            return streamingScale;
        }

        public void setStreamingScale(float streamingScale) {
            this.streamingScale = streamingScale;
        }

        public float getStreamingOffsetX() {
            return streamingOffsetX;
        }

        public void setStreamingOffsetX(float streamingOffsetX) {
            this.streamingOffsetX = streamingOffsetX;
        }

        public float getStreamingOffsetY() {
            return streamingOffsetY;
        }

        public void setStreamingOffsetY(float streamingOffsetY) {
            this.streamingOffsetY = streamingOffsetY;
        }

        public float getStreamingOffsetZ() {
            return streamingOffsetZ;
        }

        public void setStreamingOffsetZ(float streamingOffsetZ) {
            this.streamingOffsetZ = streamingOffsetZ;
        }

        public float getStreamingRotateX() {
            return streamingRotateX;
        }

        public void setStreamingRotateX(float streamingRotateX) {
            this.streamingRotateX = streamingRotateX;
        }

        public float getStreamingRotateY() {
            return streamingRotateY;
        }

        public void setStreamingRotateY(float streamingRotateY) {
            this.streamingRotateY = streamingRotateY;
        }

        public float getStreamingRotateZ() {
            return streamingRotateZ;
        }

        public void setStreamingRotateZ(float streamingRotateZ) {
            this.streamingRotateZ = streamingRotateZ;
        }

        public boolean isStreamingViewMirror() {
            return streamingViewMirror;
        }

        public void setStreamingViewMirror(boolean streamingViewMirror) {
            this.streamingViewMirror = streamingViewMirror;
        }

        public boolean isStreamingModelTransparent() {
            return streamingModelTransparent;
        }

        public void setStreamingModelTransparent(boolean streamingModelTransparent) {
            this.streamingModelTransparent = streamingModelTransparent;
        }

        public float getStreamingTextureInfluence() {
            return streamingTextureInfluence;
        }

        public void setStreamingTextureInfluence(float streamingTextureInfluence) {
            this.streamingTextureInfluence = streamingTextureInfluence;
        }

        public float getColorInfluence() {
            return ColorInfluence;
        }

        public void setColorInfluence(float colorInfluence) {
            ColorInfluence = colorInfluence;
        }

        public int getAlphaMapResId() {
            return alphaMapResId;
        }

        public void setAlphaMapResId(int alphaMapResId) {
            this.alphaMapResId = alphaMapResId;
        }

        public int getBuildInType() {
            return buildInType;
        }

        public void setBuildInType(int buildInType) {
            this.buildInType = buildInType;
        }

        public float getBuildInWidth() {
            return buildInWidth;
        }

        public void setBuildInWidth(float buildInWidth) {
            this.buildInWidth = buildInWidth;
        }

        public float getBuildInHeight() {
            return buildInHeight;
        }

        public void setBuildInHeight(float buildInHeight) {
            this.buildInHeight = buildInHeight;
        }

        public int getBuildInSegmentsW() {
            return buildInSegmentsW;
        }

        public void setBuildInSegmentsW(int buildInSegmentsW) {
            this.buildInSegmentsW = buildInSegmentsW;
        }

        public int getBuildInSegmentsH() {
            return buildInSegmentsH;
        }

        public void setBuildInSegmentsH(int buildInSegmentsH) {
            this.buildInSegmentsH = buildInSegmentsH;
        }

        public int getMaterialId() {
            return materialId;
        }

        public void setMaterialId(int materialId) {
            this.materialId = materialId;
        }
    }
}
