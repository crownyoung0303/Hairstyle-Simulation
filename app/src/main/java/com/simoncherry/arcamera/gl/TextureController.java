package com.simoncherry.arcamera.gl;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.simoncherry.arcamera.filter.camera.AFilter;
import com.simoncherry.arcamera.filter.camera.GroupFilter;
import com.simoncherry.arcamera.filter.camera.NoFilter;
import com.simoncherry.arcamera.filter.camera.TextureFilter;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Simon on 2017/7/5.
 */

public class TextureController implements GLSurfaceView.Renderer {

    private Context mContext;
    private Object mSurface;
    private GLView mGLView;

    private MyRenderer mRenderer;
    private TextureFilter mEffectFilter;                           // 特效处理的Filter
    private GroupFilter mGroupFilter;                              // 中间特效
    private AFilter mShowFilter;                                   // 用来渲染输出的Filter
    private Point mDataSize;
    private Point mWindowSize;
    private AtomicBoolean isParamSet = new AtomicBoolean(false);

    private float[] callbackOM = new float[16];                   // 用于绘制回调缩放的矩阵
    private int[] mExportFrame = new int[1];                      // 创建离屏buffer，用于最后导出数据
    private int[] mExportTexture = new int[1];
    private float[] SM = new float[16];                           // 用于绘制到屏幕上的变换矩阵
    private int mShowType = MatrixUtils.TYPE_CENTER_CROP;         // 输出到屏幕上的方式
    private int mDirectionFlag = -1;

    private boolean isRecord = false;                             // 录像flag
    private boolean isShoot = false;                              // 一次拍摄flag
    private boolean isNeedFrame = false;
    private ByteBuffer[] outPutBuffer = new ByteBuffer[3];        // 用于存储回调数据的buffer
    private FrameCallback mFrameCallback;                         // 回调
    private int frameCallbackWidth, frameCallbackHeight;          // 回调数据的宽高
    private int indexOutput=0;                                    // 回调数据使用的buffer索引

    public static final int FRAME_CALLBACK_DEFAULT = 0;           // 预览和FrameCallback均应用滤镜效果
    public static final int FRAME_CALLBACK_NO_FILTER = 1;         // 预览有滤镜效果，FrameCallback没有
    public static final int FRAME_CALLBACK_FILTER = 2;            // 预览没有滤镜效果，FrameCallback有
    public static final int FRAME_CALLBACK_DISABLE = 3;           // 只有预览，禁用FrameCallback
    public static final int FRAME_CALLBACK_ONLY = 4;              // 禁用预览，只有FrameCallback

    private int mFrameCallbackType = FRAME_CALLBACK_DEFAULT;


    public TextureController(Context context) {
        this.mContext = context;
        init();
    }

    public void surfaceCreated(Object nativeWindow){
        this.mSurface = nativeWindow;
        mGLView.surfaceCreated(null);
    }

    public void surfaceChanged(int width, int height){
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;
        mGLView.surfaceChanged(null, 0, width, height);
    }

    public void surfaceDestroyed(){
        mGLView.surfaceDestroyed(null);
    }

    public Object getOutput(){
        return mSurface;
    }

    private void init(){
        mGLView = new GLView(mContext);

        //避免GLView的attachToWindow和detachFromWindow崩溃
        ViewGroup v = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {}
        };
        v.addView(mGLView);
        v.setVisibility(View.GONE);

        mEffectFilter = new TextureFilter(mContext.getResources());
        mShowFilter = new NoFilter(mContext.getResources());
        mGroupFilter = new GroupFilter(mContext.getResources());

        //设置默认的DateSize，DataSize由AiyaProvider根据数据源的图像宽高进行设置
        mDataSize = new Point(720,1280);
        mWindowSize = new Point(720,1280);
    }

    //在Surface创建前，应该被调用
    public void setDataSize(int width, int height){
        mDataSize.x = width;
        mDataSize.y = height;
    }

    public SurfaceTexture getTexture(){
        return mEffectFilter.getTexture();
    }

    public void setImageDirection(int flag){
        this.mDirectionFlag = flag;
    }

    public void setRenderer(MyRenderer renderer){
        mRenderer = renderer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEffectFilter.create();
        mGroupFilter.create();
        mShowFilter.create();

        if(!isParamSet.get()){
            if(mRenderer!=null){
                mRenderer.onSurfaceCreated(gl, config);
            }
            sdkParamSet();
        }
        calculateCallbackOM();

        mEffectFilter.setFlag(mDirectionFlag);

        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1,mExportFrame,0);
        EasyGlUtils.genTexturesWithParameter(1,mExportTexture,0,GLES20.GL_RGBA,mDataSize.x,
                mDataSize.y);
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MatrixUtils.getMatrix(SM,mShowType, mDataSize.x, mDataSize.y, width, height);

        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);

        mGroupFilter.setSize(mDataSize.x,mDataSize.y);

        mEffectFilter.setSize(mDataSize.x, mDataSize.y);
        mShowFilter.setSize(mDataSize.x,mDataSize.y);

        if(mRenderer != null){
            mRenderer.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(isParamSet.get()){
            mEffectFilter.draw();
            mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
            mGroupFilter.draw();

            //显示传入的texture上，一般是显示在屏幕上
            GLES20.glViewport(0, 0, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);

            switch (mFrameCallbackType) {
                case FRAME_CALLBACK_DEFAULT:
                case FRAME_CALLBACK_NO_FILTER:
                    mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
                    break;
                case FRAME_CALLBACK_FILTER:
                    mShowFilter.setTextureId(mEffectFilter.getOutputTexture());
                    break;
                case FRAME_CALLBACK_ONLY:
                    mShowFilter.setTextureId(0);
                    break;
            }

            mShowFilter.draw();

            if(mRenderer != null){
                mRenderer.onDrawFrame(gl);
            }
            callbackIfNeeded();
        }
    }

    public void addFilter(AFilter filter){
        mGroupFilter.addFilter(filter);
    }

    public AFilter getLastFilter() {
        return mGroupFilter.getLastFilter();
    }

    public void clearFilter() {
        mGroupFilter.clearAll();
    }

    public void setShowType(int type){
        this.mShowType = type;
        if(mWindowSize.x > 0 && mWindowSize.y > 0){
            MatrixUtils.getMatrix(SM, mShowType, mDataSize.x, mDataSize.y, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);
            mShowFilter.setSize(mWindowSize.x,mWindowSize.y);
        }
    }

    public void startRecord(){
        isRecord = true;
    }

    public void stopRecord(){
        isRecord = false;
    }

    public void takePhoto(){
        isShoot = true;
    }

    public void setNeedFrame(boolean isNeedFrame) {
        this.isNeedFrame = isNeedFrame;
    }

    public int getFrameCallbackWidth() {
        return frameCallbackWidth;
    }

    public int getFrameCallbackHeight() {
        return frameCallbackHeight;
    }

    public void setFrameCallback(int width, int height, FrameCallback frameCallback){
        this.frameCallbackWidth = width;
        this.frameCallbackHeight = height;
        if (frameCallbackWidth > 0 && frameCallbackHeight > 0) {
            if(outPutBuffer != null){
                outPutBuffer = new ByteBuffer[3];
            }
            calculateCallbackOM();
            this.mFrameCallback = frameCallback;
        } else {
            this.mFrameCallback = null;
        }
    }

    private void calculateCallbackOM(){
        if(frameCallbackHeight > 0 && frameCallbackWidth > 0 && mDataSize.x > 0 && mDataSize.y > 0){
            //计算输出的变换矩阵
            MatrixUtils.getMatrix(callbackOM, MatrixUtils.TYPE_CENTER_CROP, mDataSize.x, mDataSize.y,
                    frameCallbackWidth,
                    frameCallbackHeight);
            MatrixUtils.flip(callbackOM, false, true);
        }
    }

    public Point getWindowSize(){
        return mWindowSize;
    }

    private void sdkParamSet(){
        if(!isParamSet.get()&&mDataSize.x>0&&mDataSize.y>0) {
            isParamSet.set(true);
        }
    }

    public void setFrameCallbackType(int type) {
        mFrameCallbackType = type;
    }

    //需要回调，则缩放图片到指定大小，读取数据并回调
    private void callbackIfNeeded() {
        if (mFrameCallback != null && (isRecord || isShoot || isNeedFrame)) {
            indexOutput = indexOutput++ >= 2 ? 0 : indexOutput;
            if (outPutBuffer[indexOutput] == null) {
                outPutBuffer[indexOutput] = ByteBuffer.allocate(frameCallbackWidth *
                        frameCallbackHeight*4);
            }
            GLES20.glViewport(0, 0, frameCallbackWidth, frameCallbackHeight);
            EasyGlUtils.bindFrameTexture(mExportFrame[0],mExportTexture[0]);

            switch (mFrameCallbackType) {
                case FRAME_CALLBACK_DEFAULT:
                case FRAME_CALLBACK_FILTER:
                case FRAME_CALLBACK_ONLY:
                    mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
                    break;
                case FRAME_CALLBACK_NO_FILTER:
                    mShowFilter.setTextureId(mEffectFilter.getOutputTexture());
                    break;
            }

            mShowFilter.setMatrix(callbackOM);
            mShowFilter.draw();
            frameCallback();
            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            mShowFilter.setMatrix(SM);
        }
    }

    //读取数据并回调
    private void frameCallback(){
        GLES20.glReadPixels(0, 0, frameCallbackWidth, frameCallbackHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outPutBuffer[indexOutput]);
        ByteBuffer byteBuffer = outPutBuffer[indexOutput];
        if (byteBuffer != null) {
            mFrameCallback.onFrame(byteBuffer.array(), 0);
        }
    }

    public void create(int width, int height){
        mGLView.attachedToWindow();
        surfaceCreated(mSurface);
        surfaceChanged(width,height);
    }

    public void destroy(){
        if(mRenderer != null){
            mRenderer.onDestroy();
        }
        mGLView.surfaceDestroyed(null);
        mGLView.detachedFromWindow();
        mGLView.clear();
    }

    public void requestRender(){
        mGLView.requestRender();
    }

    public void onPause(){
        mGLView.onPause();
    }

    public void onResume(){
        mGLView.onResume();
    }

    private class GLView extends GLSurfaceView{

        public GLView(Context context) {
            super(context);
            init();
        }

        private void init(){
            getHolder().addCallback(null);
            setEGLWindowSurfaceFactory(new GLSurfaceView.EGLWindowSurfaceFactory() {
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig
                        config, Object window) {
                    return egl.eglCreateWindowSurface(display, config, mSurface, null);
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                    egl.eglDestroySurface(display, surface);
                }
            });
            setEGLContextClientVersion(2);
            setRenderer(TextureController.this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            setPreserveEGLContextOnPause(true);
        }

        public void attachedToWindow(){
            super.onAttachedToWindow();
        }

        public void detachedFromWindow(){
            super.onDetachedFromWindow();
        }

        public void clear(){}
    }
}
