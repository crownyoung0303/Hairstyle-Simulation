package com.hairstyle.simu.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.hairstyle.simu.model.DynamicPoint;
import com.hairstyle.simu.model.Ornament;
import com.hairstyle.simu.rajawali.MyFragmentShader;
import com.hairstyle.simu.rajawali.MyVertexShader;
import com.hairstyle.simu.util.BitmapUtils;
import com.hairstyle.simu.util.MaterialFactory;
import com.hairstyle.simu.util.OrnamentFactory;

import org.rajawali3d.Geometry3D;
import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.plugins.IMaterialPlugin;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class My3DRenderer extends Renderer implements OnObjectPickedListener, StreamingTexture.ISurfaceListener {
    private final static String TAG = My3DRenderer.class.getSimpleName();

    private Object3D mContainer;
    private List<Object3D> mObject3DList = new ArrayList<>();
    private Object3D mPickedObject;
    private Object3D mShaderPlane;
    private Material mCustomMaterial;
    private MyFragmentShader mMyFragmentShader;

    private Ornament mOrnamentModel;
    private boolean mIsNeedUpdateOrnament = false;
    private boolean mIsOrnamentVisible = true;
    private int mScreenW = 1;
    private int mScreenH = 1;
    private int mSkinColor = 0xffd4c9b5;

    private int mModelType = Ornament.MODEL_TYPE_NONE;
    private Vector3 mAccValues;
    private float mTransX = 0.0f;
    private float mTransY = 0.0f;
    private float mScale = 1.0f;
    private List<Geometry3D> mGeometry3DList = new ArrayList<>();
    private List<DynamicPoint> mPoints = new ArrayList<>();
    private boolean mIsChanging = false;
    private List<Material> mMaterialList = new ArrayList<>();
    private float mMaterialTime = 0;
    private ObjectColorPicker mPicker;

    // StreamingTexture
    private Surface mSurface;
    private View mStreamingView;
    private Handler mStreamingHandler;
    private StreamingTexture mStreamingTexture;
    private volatile boolean mShouldUpdateTexture;
    private final float[] mMatrix = new float[16];
    private boolean mIsStreamingViewMirror = false;

    private final Runnable mUpdateTexture = new Runnable() {
        public void run() {
            // -- Draw the view on the canvas
            if (mSurface != null && mStreamingTexture != null && mStreamingView != null) {
                try {
                    final Canvas canvas = mSurface.lockCanvas(null);
                    canvas.translate(mStreamingView.getScrollX(), -mStreamingView.getScrollY());
                    if (mIsStreamingViewMirror) {
                        canvas.scale(-1, 1, mStreamingView.getWidth() / 2, mStreamingView.getHeight() / 2);
                    }
                    mStreamingTexture.getSurfaceTexture().getTransformMatrix(mMatrix);
                    mStreamingView.draw(canvas);
                    mSurface.unlockCanvasAndPost(canvas);
                    // -- Indicates that the texture should be updated on the OpenGL thread.
                    mShouldUpdateTexture = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public My3DRenderer(Context context) {
        super(context);
        mAccValues = new Vector3();
    }

    public void setOrnamentModel(Ornament mOrnamentModel) {
        this.mOrnamentModel = mOrnamentModel;
    }

    public void setIsNeedUpdateOrnament(boolean mIsNeedUpdateOrnament) {
        this.mIsNeedUpdateOrnament = mIsNeedUpdateOrnament;
    }

    public void setIsOrnamentVisible(boolean mIsOrnamentVisible) {
        this.mIsOrnamentVisible = mIsOrnamentVisible;
    }

    public void setAccelerometerValues(float x, float y, float z) {
        mAccValues.setAll(x, y, z);
    }

    public void setTransition(float x, float y, float z) {
        if (mModelType == Ornament.MODEL_TYPE_STATIC || mModelType == Ornament.MODEL_TYPE_SHADER) {
            mTransX = x;
            mTransY = y;
            setScale(z);
        }
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public void setScreenW(int width) {
        mScreenW = width;
    }

    public void setScreenH(int height) {
        mScreenH = height;
    }

    public void setSkinColor(int mSkinColor) {
        this.mSkinColor = mSkinColor;
    }

    public void setStreamingView(View streamingView) {
        this.mStreamingView = streamingView;
    }

    public void setStreamingHandler(Handler streamingHandler) {
        this.mStreamingHandler = streamingHandler;
    }

    @Override
    protected void initScene() {
        try {
            mContainer = new Object3D();
            getCurrentScene().addChild(mContainer);
            getCurrentScene().getCamera().setZ(5.5);

        } catch (Exception e) {
            e.printStackTrace();
        }

        getCurrentScene().setBackgroundColor(0);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        if (mIsNeedUpdateOrnament) {
            mIsNeedUpdateOrnament = false;
            loadOrnament();
        }

        if (mModelType == Ornament.MODEL_TYPE_STATIC || mModelType == Ornament.MODEL_TYPE_SHADER) {
            if (mOrnamentModel != null) {
                if (mOrnamentModel.isEnableRotation()) {
                    mContainer.setRotation(mAccValues.x, mAccValues.y, mAccValues.z);
                }

                if (mOrnamentModel.isEnableScale()) {
                    mContainer.setScale(mScale);
                }

                if (mOrnamentModel.isEnableTransition()) {
                    getCurrentCamera().setX(mTransX);
                    getCurrentCamera().setY(mTransY);
                }
            }

            if (mOrnamentModel != null && mOrnamentModel.getTimeStep() > 0 && mMaterialList != null) {
                for (int i = 0; i < mMaterialList.size(); i++) {
                    Material material = mMaterialList.get(i);
                    if (material != null) {
                        material.setTime(mMaterialTime);
                        mMaterialTime += mOrnamentModel.getTimeStep();
                        if (mMaterialTime > 1000) {
                            mMaterialTime = 0;
                        }
                    }
                }
            }

        } else if (mModelType == Ornament.MODEL_TYPE_DYNAMIC) {
            if (!mIsChanging && mPoints != null && mPoints.size() > 0) {
                mIsChanging = true;

                try {  // FIXME
                    if (mGeometry3DList != null && mGeometry3DList.size() > 0) {
                        for (Geometry3D geometry3D : mGeometry3DList) {
                            FloatBuffer vertBuffer = geometry3D.getVertices();
                            for (int i = 0; i < mPoints.size(); i++) {
                                DynamicPoint point = mPoints.get(i);
                                changePoint(vertBuffer, point.getIndex(), point.getX(), point.getY(), point.getZ());
                            }
                            geometry3D.changeBufferData(geometry3D.getVertexBufferInfo(), vertBuffer, 0, vertBuffer.limit());
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                mIsChanging = false;
            }
        }

        // TODO
        if (mShaderPlane != null && mOrnamentModel != null && mMyFragmentShader != null && mCustomMaterial != null) {
            mMyFragmentShader.setScreenW(mScreenW);
            mMyFragmentShader.setScreenH(mScreenH);

            if (mMaterialTime == 0) {
                mMyFragmentShader.setFlag(1);
            }

            mMaterialTime += mOrnamentModel.getTimeStep();
            mCustomMaterial.setTime(mMaterialTime);

            if (mMaterialTime > mOrnamentModel.getTimePeriod()) {
                mMyFragmentShader.setFlag(0);
            }

            if (mMaterialTime > 1) {
                mMaterialTime = 0;
            }
        }

        // -- not a really accurate way of doing things but you get the point :)
        if (mSurface != null && mStreamingHandler != null && mFrameCount++ >= (mFrameRate * 0.25)) {
            mFrameCount = 0;
            mStreamingHandler.post(mUpdateTexture);
        }
        // -- update the texture because it is ready
        if (mShouldUpdateTexture) {
            if (mStreamingTexture != null) {
                mStreamingTexture.update();
            }
            mShouldUpdateTexture = false;
        }

        if (mPickedObject != null && mOrnamentModel != null && mObject3DList != null && mObject3DList.size() > 0) {
            int index = mObject3DList.indexOf(mPickedObject);
            if (index >= 0) {
                List<Ornament.Model> modelList = mOrnamentModel.getModelList();
                if (modelList != null && modelList.size() > index) {
                    Ornament.Model model = modelList.get(index);
                    if (model != null && model.isNeedObjectPick()) {
                        boolean isPicked = model.isPicked();
                        if (isPicked) {
                            mPickedObject.setPosition(model.getAfterX(), model.getAfterY(), model.getAfterZ());
                            mPickedObject.setRotation(model.getAxisX(), model.getAxisY(), model.getAxisZ(),
                                    model.getAfterAngle());
                        } else {
                            mPickedObject.setPosition(model.getBeforeX(), model.getBeforeY(), model.getBeforeZ());
                            mPickedObject.setRotation(model.getAxisX(), model.getAxisY(), model.getAxisZ(),
                                    model.getBeforeAngle());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        Log.i(TAG, "onObjectPicked: " + object.getName());
        mPickedObject = object;
        if (mOrnamentModel != null && mObject3DList != null && mObject3DList.size() > 0) {
            int index = mObject3DList.indexOf(object);
            if (index >= 0) {
                List<Ornament.Model> modelList = mOrnamentModel.getModelList();
                if (modelList != null && modelList.size() > index) {
                    Ornament.Model model = modelList.get(index);
                    if (model != null && model.isNeedObjectPick()) {
                        boolean isPicked = model.isPicked();
                        isPicked = !isPicked;
                        model.setPicked(isPicked);
                    }
                }
            }
        }
    }

    @Override
    public void onNoObjectPicked() {
    }

    @Override
    public void setSurface(Surface surface) {
        mSurface = surface;
        if (mStreamingTexture != null && mStreamingView != null) {
            mStreamingTexture.getSurfaceTexture().setDefaultBufferSize(mStreamingView.getWidth(), mStreamingView.getHeight());
        }
    }

    private void clearScene() {
        if (mObject3DList != null && mObject3DList.size() > 0) {
            for (int i = 0; i < mObject3DList.size(); i++) {
                Object3D object3D = mObject3DList.get(i);
                if (object3D != null) {
                    if (mPicker != null) {
                        mPicker.unregisterObject(object3D);
                    }
                    mContainer.removeChild(object3D);
                    object3D.destroy();
                }
            }
            mObject3DList.clear();
        }

        mPicker = null;
        mPickedObject = null;

        if (mMaterialList != null && mMaterialList.size() > 0) {
            mMaterialList.clear();
        }

        if (mGeometry3DList != null && mGeometry3DList.size() > 0) {
            mGeometry3DList.clear();
        }

        if (mShaderPlane != null) {
            mContainer.removeChild(mShaderPlane);
            mShaderPlane = null;
        }

        mIsStreamingViewMirror = false;
        mStreamingTexture = null;

        mMaterialTime = 0;
    }

    private void loadOrnament() {
        try {
            clearScene();

            if (mOrnamentModel != null) {
                mModelType = mOrnamentModel.getType();
                switch (mModelType) {
                    case Ornament.MODEL_TYPE_SHADER:
                        loadShaderMaterialModel();
                        break;
                    case Ornament.MODEL_TYPE_STATIC:
                    case Ornament.MODEL_TYPE_DYNAMIC:
                        loadNormalMaterialModel();
                        initOrnamentParams();
                        break;
                }

                boolean isHasShaderPlane = mOrnamentModel.isHasShaderPlane();
                if (isHasShaderPlane) {
                    loadShaderPlane();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadShaderMaterialModel() {
        try {
            List<Object3D> object3DList = mOrnamentModel.getObject3DList();
            List<List<IMaterialPlugin>> materialList = mOrnamentModel.getMaterialList();
            if (object3DList != null && materialList != null) {
                mObject3DList.addAll(object3DList);

                for (List<IMaterialPlugin> pluginList : materialList) {
                    Material material = new Material();
                    material.enableTime(true);
                    for (IMaterialPlugin plugin : pluginList) {
                        material.addPlugin(plugin);
                    }
                    mMaterialList.add(material);
                }

                if (mObject3DList != null && mObject3DList.size() > 0) {
                    for (int i = 0; i < mObject3DList.size(); i++) {
                        Object3D object3D = mObject3DList.get(i);
                        if (object3D != null) {
                            Material material = mMaterialList.get(i);
                            if (material != null) {
                                object3D.setMaterial(material);
                            }
                            mContainer.addChild(object3D);
                        }
                    }

                    mContainer.setScale(mOrnamentModel.getScale());
                    mContainer.setPosition(mOrnamentModel.getOffsetX(), mOrnamentModel.getOffsetY(), mOrnamentModel.getOffsetZ());
                    mContainer.setRotation(mOrnamentModel.getRotateX(), mOrnamentModel.getRotateY(), mOrnamentModel.getRotateZ());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNormalMaterialModel() {
        try {
            List<Ornament.Model> modelList = mOrnamentModel.getModelList();
            if (modelList != null && modelList.size() > 0) {
                for (Ornament.Model model : modelList) {
                    String texturePath = model.getTexturePath();

                    Object3D object3D;
                    if (texturePath != null) {
                        object3D = loadDynamicModel(model);
                    } else {
                        object3D = loadStaticModel(model);
                    }

                    if (object3D != null) {
                        mObject3DList.add(object3D);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object3D loadStaticModel(Ornament.Model model) {
        try {
            Object3D object3D;
            int modelResId = model.getModelResId();
            int buildInType = model.getBuildInType();
            if (modelResId != -1) {
                object3D = getExternalModel(modelResId);
            } else if (buildInType != -1) {
                object3D = getBuildInModel(model, buildInType);
            } else {
                throw new RuntimeException("invalid object3d");
            }

            setModelBaseParams(model, object3D);
            setModelTexture(model, object3D);
            setModelMaterial(model, object3D);
            setModelColor(model, object3D);
            handleObjectPicking(model, object3D);
            handleStreamingTexture(model);

            return object3D;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Object3D getExternalModel(int modelResId) throws ParsingException {
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(), mTextureManager, modelResId);
        objParser.parse();
        return objParser.getParsedObject();
    }

    private Object3D getBuildInModel(Ornament.Model model, int buildInType) {
        switch (buildInType) {
            case Ornament.Model.BUILD_IN_PLANE:
                return new Plane(model.getBuildInWidth(), model.getBuildInHeight(),
                        model.getBuildInSegmentsW(), model.getBuildInSegmentsH());
            case Ornament.Model.BUILD_IN_CUBE:
                return new Cube(model.getBuildInWidth());
            case Ornament.Model.BUILD_IN_SPHERE:
                return new Sphere(model.getBuildInWidth(),
                        model.getBuildInSegmentsW(), model.getBuildInSegmentsH());
            default:
                throw new RuntimeException("invalid object3d");
        }
    }

    private void setModelBaseParams(Ornament.Model model, Object3D object3D) {
        String name = model.getName();
        object3D.setName(name == null ? "" : name);
        object3D.setScale(model.getScale());
        object3D.setPosition(model.getOffsetX(), model.getOffsetY(), model.getOffsetZ());
        object3D.setRotation(model.getRotateX(), model.getRotateY(), model.getRotateZ());
    }

    private void setModelTexture(Ornament.Model model, Object3D object3D) throws ATexture.TextureException {
        int textureResId = model.getTextureResId();
        if (textureResId > 0) {
            ATexture texture = object3D.getMaterial().getTextureList().get(0);
            object3D.getMaterial().removeTexture(texture);

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), textureResId);
            if (bitmap != null) {
                mIsChanging = true;
                if (model.isNeedSkinColor()) {
                    bitmap = changeSkinColor(bitmap, mSkinColor);
                }
                object3D.getMaterial().addTexture(new Texture("canvas", bitmap));
                mIsChanging = false;
            }
        }
    }

    private void setModelMaterial(Ornament.Model model, Object3D object3D) {
        int materialId = model.getMaterialId();
        if (materialId > -1) {
            object3D.setMaterial(MaterialFactory.getMaterialById(materialId));
        }
    }

    private void setModelColor(Ornament.Model model, Object3D object3D) {
        int color = model.getColor();
        if (color != OrnamentFactory.NO_COLOR) {
            object3D.getMaterial().setColor(color);
        }
    }

    private void handleObjectPicking(Ornament.Model model, Object3D object3D) {
        if (model.isNeedObjectPick()) {
            if (mPicker == null) {
                mPicker = new ObjectColorPicker(this);
                mPicker.setOnObjectPickedListener(this);
            }
            mPicker.registerObject(object3D);
        }
    }

    private void handleStreamingTexture(Ornament.Model model) throws ATexture.TextureException {
        if (model.isNeedStreaming()) {
            Object3D streamingModel;
            int modelType = model.getStreamingModelType();
            float modelWidth = model.getStreamingModelWidth();
            float modelHeight = model.getStreamingModelHeight();
            int modelSegmentsW = model.getStreamingModelSegmentsW();
            int modelSegmentsH = model.getStreamingModelSegmentsH();
            switch (modelType) {
                case Ornament.Model.STREAMING_PLANE_MODEL:
                    streamingModel = new Plane(modelWidth, modelHeight, modelSegmentsW, modelSegmentsH);
                    break;
                case Ornament.Model.STREAMING_SPHERE_MODEL:
                    streamingModel = new Sphere(modelWidth, modelSegmentsW, modelSegmentsH);
                    break;
                default:
                    throw new RuntimeException("invalid streaming model");
            }

            streamingModel.setTransparent(model.isStreamingModelTransparent());
            streamingModel.setColor(0);
            streamingModel.setScale(model.getScale());
            streamingModel.setPosition(model.getStreamingOffsetX(), model.getStreamingOffsetY(),model.getStreamingOffsetZ());
            streamingModel.setRotation(model.getStreamingRotateX(), model.getStreamingRotateY(), model.getStreamingRotateZ());
            streamingModel.setRenderChildrenAsBatch(true);
            if (mStreamingTexture == null) {
                mStreamingTexture = new StreamingTexture("viewTexture", this);
            }
            mStreamingTexture.setInfluence(model.getStreamingTextureInfluence());
            Material material = new Material();
            material.setColorInfluence(model.getColorInfluence());
            try {
                material.addTexture(mStreamingTexture);
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }

            if (model.getAlphaMapResId() > 0) {
                material.addTexture(new AlphaMapTexture("alphaMapTex", model.getAlphaMapResId()));
            }
            streamingModel.setMaterial(material);
            mContainer.addChild(streamingModel);
            mObject3DList.add(streamingModel);

            mIsStreamingViewMirror = model.isStreamingViewMirror();
        }
    }

    private Object3D loadDynamicModel(Ornament.Model model) {
        try {
            String objDir = "OpenGLDemo/txt/";
            String objName = "base_face_uv3_obj";
            LoaderOBJ parser = new LoaderOBJ(this, objDir + objName);
            parser.parse();
            Object3D object3D = parser.getParsedObject();

            object3D.setScale(model.getScale());
            object3D.setPosition(model.getOffsetX(), model.getOffsetY(), model.getOffsetZ());
            object3D.setRotation(model.getRotateX(), model.getRotateY(), model.getRotateZ());

            ATexture texture = object3D.getMaterial().getTextureList().get(0);
            object3D.getMaterial().removeTexture(texture);

            String texturePath = model.getTexturePath();
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFilePath(texturePath, 300, 300);
            if (model.isNeedSkinColor()) {
                bitmap = changeSkinColor(bitmap, mSkinColor);
            }
            object3D.getMaterial().addTexture(new Texture("canvas", bitmap));
            object3D.getMaterial().enableLighting(false);

            int color = model.getColor();
            if (color != OrnamentFactory.NO_COLOR) {
                object3D.getMaterial().setColor(color);
            }

            return object3D;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initOrnamentParams() {
        if (mObject3DList != null && mObject3DList.size() > 0) {
            for (Object3D object3D : mObject3DList) {
                mContainer.addChild(object3D);

                Geometry3D geometry3D = object3D.getGeometry();
                mGeometry3DList.add(geometry3D);
            }
        }

        mContainer.setTransparent(false);
        mContainer.setScale(1.0f);
        mContainer.setRotation(0, 0, 0);
        mContainer.setPosition(0, 0, 0);
        getCurrentCamera().setX(0);
        getCurrentCamera().setY(0);
    }

    private void loadShaderPlane() {
        int vertResId = mOrnamentModel.getVertResId();
        int fragResId = mOrnamentModel.getFragResId();
        if (vertResId > 0 && fragResId > 0) {
            mMyFragmentShader = new MyFragmentShader(fragResId);

            mCustomMaterial = new Material(
                    new MyVertexShader(vertResId),
                    mMyFragmentShader);
            mCustomMaterial.enableTime(true);

            float offsetX = mOrnamentModel.getPlaneOffsetX();
            float offsetY = mOrnamentModel.getPlaneOffsetY();
            float offsetZ = mOrnamentModel.getPlaneOffsetZ();
            mShaderPlane = new Plane(5, 5, 1, 1);
            mShaderPlane.setPosition(offsetX, offsetY, offsetZ);
            mShaderPlane.setMaterial(mCustomMaterial);
            mShaderPlane.setTransparent(true);
            mContainer.addChild(mShaderPlane);
        }
    }

    private int faceIndices[][]={
            {66, 68, 123, 125, 128, 132, 135, 137},
            {57, 59, 63, 64, 110, 114, 116, 120, 124},
            {51, 53, 67, 71, 86, 90, 92, 96, 121},
            {54, 56, 65, 69},
            {35, 39, 41, 45, 49, 52, 55, 58},
            {15, 70, 122, 129, 146, 152, 158},
            {17, 61, 126, 136, 150, 156, 162},
            {139, 144},
            {141, 142, 147, 149, 151, 154},
            {153, 155, 157, 160},
            {33, 34, 37, 99, 101, 105, 107},
            {100, 103},
            {36, 60, 97, 109},
            {112, 115},
            {16, 21, 24, 27, 30, 31, 62, 106, 118},
            {40, 43, 47, 75, 77, 81, 84},
            {76, 79},
            {44, 50, 83, 94},
            {88, 91},
            {2, 6, 9, 12, 13, 46, 72, 73, 85},
            {38, 42},
            {1, 4},
            {5, 7},
            {8, 10},
            {11, 14, 159},
            {18, 19, 161},
            {20, 22},
            {23, 25},
            {26, 28},
            {3, 48},
            {29, 32},
            {80, 82},
            {74, 78},
            {93, 95},
            {87, 89},
            {98, 102},
            {104, 108},
            {117, 119},
            {111, 113},
            {140, 145},
            {143, 148},
            {127, 130},
            {131, 133},
            {134, 138},
    };

    private int[] getIndexArrayByFace(int faceIndex) {
        return faceIndices[faceIndex];
    }

    private void changePoint(FloatBuffer vertBuffer, int faceIndex, float x, float y, float z) {
        int[] indices = getIndexArrayByFace(faceIndex);
        if (indices != null) {
            int len = indices.length;
            for (int i=0; i<len; i++) {
                int index = indices[i]-1;
                vertBuffer.put(index * 3, x);
                vertBuffer.put(index * 3 + 1, y);
                vertBuffer.put(index * 3 + 2, z);
            }
        }
    }

    public void setDynamicPoints(List<DynamicPoint> mPoints) {
        if (!mIsChanging) {
            this.mPoints = mPoints;
        }
    }

    private Bitmap changeSkinColor(Bitmap bitmap, int skinColor) {
        if (bitmap != null) {
            Bitmap texture = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            int width = texture.getWidth();
            int height = texture.getHeight();

            int skinRed = Color.red(skinColor);
            int skinGreen = Color.green(skinColor);
            int skinBlue = Color.blue(skinColor);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = texture.getPixel(x, y);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    // TODO
                    red = overlay(skinRed, red);
                    green = overlay(skinGreen, green);
                    blue = overlay(skinBlue, blue);

                    pixel = Color.rgb(red, green, blue);
                    texture.setPixel(x, y, pixel);
                }
            }

            float saturation = 0.35f;
            ColorMatrix cMatrix = new ColorMatrix();
            cMatrix.setSaturation(saturation);

            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

            Canvas canvas = new Canvas(texture);
            canvas.drawBitmap(texture, 0, 0, paint);

            return texture;
        }
        return null;
    }

    private int softLight(int A, int B) {
        return (B < 128) ? (2 * ((A >> 1) + 64)) * (B / 255) : (255 - (2 * (255 - ((A >> 1) + 64)) * (255 - B) / 255));
    }

    private int overlay(int A, int B) {
        return ((B < 128) ? (2 * A * B / 255) : (255 - 2 * (255 - A) * (255 - B) / 255));
    }

    public void getObjectAt(float x, float y) {
        if (mPicker != null) {
            mPicker.getObjectAt(x, y);
        }
    }
}
