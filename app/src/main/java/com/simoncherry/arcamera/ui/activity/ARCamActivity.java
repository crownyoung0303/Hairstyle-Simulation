package com.simoncherry.arcamera.ui.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.simoncherry.arcamera.MediaLoaderCallback;
import com.simoncherry.arcamera.R;
import com.simoncherry.arcamera.codec.CameraRecorder;
import com.simoncherry.arcamera.contract.ARCamContract;
import com.simoncherry.arcamera.filter.camera.AFilter;
import com.simoncherry.arcamera.filter.camera.FilterFactory;
import com.simoncherry.arcamera.filter.camera.LandmarkFilter;
import com.simoncherry.arcamera.gl.Camera1Renderer;
import com.simoncherry.arcamera.gl.CameraTrackRenderer;
import com.simoncherry.arcamera.gl.FrameCallback;
import com.simoncherry.arcamera.gl.My3DRenderer;
import com.simoncherry.arcamera.gl.MyRenderer;
import com.simoncherry.arcamera.gl.TextureController;
import com.simoncherry.arcamera.model.DynamicPoint;
import com.simoncherry.arcamera.model.Filter;
import com.simoncherry.arcamera.model.Ornament;
import com.simoncherry.arcamera.model.Photo;
import com.simoncherry.arcamera.presenter.ARCamPresenter;
import com.simoncherry.arcamera.ui.adapter.FilterAdapter;
import com.simoncherry.arcamera.ui.adapter.OrnamentAdapter;
import com.simoncherry.arcamera.ui.adapter.PhotoAdapter;
import com.simoncherry.arcamera.ui.custom.CircularProgressView;
import com.simoncherry.arcamera.ui.custom.CustomBottomSheet;
import com.simoncherry.arcamera.util.Accelerometer;
import com.simoncherry.arcamera.util.FileUtils;
import com.simoncherry.arcamera.util.LandmarkUtils;
import com.simoncherry.arcamera.util.OrnamentFactory;
import com.simoncherry.arcamera.util.PermissionUtils;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.ISurface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ARCamActivity extends AppCompatActivity implements ARCamContract.View, FrameCallback {

    private final static String TAG = ARCamActivity.class.getSimpleName();
    // 拍照尺寸
    private final static int IMAGE_WIDTH = 720;
    private final static int IMAGE_HEIGHT = 1280;
    // 录像尺寸
    private final static int VIDEO_WIDTH = 384;
    private final static int VIDEO_HEIGHT = 640;
    // SenseTime人脸检测最大支持的尺寸为 640 x 480
    private final static int PREVIEW_WIDTH = 640;
    private final static int PREVIEW_HEIGHT = 480;

    private final static int TYPE_NONE = -1;
    private final static int TYPE_PHOTO = 0;
    private final static int TYPE_RECORD = 1;

    private Context mContext;
    private ARCamPresenter mPresenter;

    private RelativeLayout mLayoutRoot;
    // 用于渲染相机预览画面
    private SurfaceView mSurfaceView;
    // 用于显示人脸检测参数
    private TextView mTrackText, mActionText;
    // 处理滤镜逻辑
    protected TextureController mController;
    private MyRenderer mRenderer;
    // 相机Id，后置是0，前置是1
    private int cameraId = 1;
    // 默认的相机滤镜的Id
    protected int mCurrentFilterId = R.id.menu_camera_default;
    // 加速度计工具类，似乎是用于人脸检测的
    private static Accelerometer mAccelerometer;

    // 由于不会写显示3D模型的滤镜，暂时借助于OpenGL ES引擎Rajawali
    // 用于渲染3D模型
    private ISurface mRenderSurface;
    private ISurfaceRenderer mISurfaceRenderer;
    // 因为渲染相机和3D模型的SurfaceView是分开的，拍照/录像时只能分别取两路数据，再合并
    // 拍照用的
    private Bitmap mRajawaliBitmap = null;
    // 录像用的
    private int[] mRajawaliPixels = null;

    // 拍照/录像按钮
    private CircularProgressView mCapture;
    // 处理录像逻辑
    private CameraRecorder mp4Recorder;
    private ExecutorService mExecutor;
    // 录像持续的时间
    private long time;
    // 录像最长时间限制
    private long maxTime = 20000;
    // 步长
    private long timeStep = 50;
    // 录像标志位
    private boolean recordFlag = false;
    // 处理帧数据的标志位 0为拍照 1为录像
    private int mFrameType = TYPE_NONE;

    // 滤镜列表
    private CustomBottomSheet mFilterSheet;
    private RecyclerView mRvFilter;
    private FilterAdapter mFilterAdapter;
    private List<Filter> mFilters;

    // 装饰品列表
    private CustomBottomSheet mOrnamentSheet;
    private RecyclerView mRvOrnament;
    private OrnamentAdapter mOrnamentAdapter;
    private List<Ornament> mOrnaments;

    // 特效列表
    private CustomBottomSheet mEffectSheet;
    private RecyclerView mRvEffect;
    private FilterAdapter mEffectAdapter;
    private List<Filter> mEffects;

    // 面具列表
    private CustomBottomSheet mMaskSheet;
    private RecyclerView mRvMask;
    private PhotoAdapter mMaskAdapter;
    private List<Photo> mMasks;
    private MediaLoaderCallback mediaLoaderCallback = null;
    private STMobileMultiTrack106 tracker;
    private static final int ST_MOBILE_TRACKING_ENABLE_FACE_ACTION = 0x00000020;

    private boolean mIsNeedSkinColor = false;
    private PointF mSamplePoint = null;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private boolean mIsNeedFrameCallback = false;
    private View mStreamingView;
    private Handler mStreamingHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ARCamActivity.this;
        mPresenter = new ARCamPresenter(this);
        // 用于人脸检测
        mAccelerometer = new Accelerometer(this);
        mAccelerometer.start();

        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10, initViewRunnable);
    }

    protected void setContentView(){
        setContentView(R.layout.activity_ar_cam);
        initSurfaceView();
        initRajawaliSurface();
        initCaptureButton();
        initMenuButton();
        initCommonView();
        initFilterSheet();
        initOrnamentSheet();
        initEffectSheet();
        initMaskSheet();
    }

    private void initSurfaceView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface);
        // 按住屏幕取消滤镜，松手恢复滤镜，以便对比
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mController.clearFilter();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mController.addFilter(FilterFactory.getFilter(getResources(), mCurrentFilterId));
                        break;
                }
                return true;
            }
        });
    }

    private void initRajawaliSurface() {
        mRenderSurface = (org.rajawali3d.view.SurfaceView) findViewById(R.id.rajwali_surface);
        // 将Rajawali的SurfaceView的背景设为透明
        ((org.rajawali3d.view.SurfaceView) mRenderSurface).setTransparent(true);
        // 将Rajawali的SurfaceView的尺寸设为录像的尺寸
        ((org.rajawali3d.view.SurfaceView) mRenderSurface).getHolder().setFixedSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        mISurfaceRenderer = new My3DRenderer(this);
        ((My3DRenderer) mISurfaceRenderer).setScreenW(IMAGE_WIDTH);
        ((My3DRenderer) mISurfaceRenderer).setScreenH(IMAGE_HEIGHT);
        mRenderSurface.setSurfaceRenderer(mISurfaceRenderer);
        ((org.rajawali3d.view.SurfaceView) mRenderSurface).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float scaleW = VIDEO_WIDTH / (float) mSurfaceWidth;
                    float scaleH = VIDEO_HEIGHT / (float) mSurfaceHeight;
                    float touchX = event.getX() * scaleW;
                    float touchY = event.getY() * scaleH;
                    ((My3DRenderer) mISurfaceRenderer).getObjectAt(touchX, touchY);
                }
                return onTouchEvent(event);
            }
        });

        // 拍照时，先取Rajawali的帧数据，转成Bitmap待用；再取相机预览的帧数据，最后合成
        ((org.rajawali3d.view.SurfaceView) mRenderSurface).setOnTakeScreenshotListener(new org.rajawali3d.view.SurfaceView.OnTakeScreenshotListener() {
            @Override
            public void onTakeScreenshot(Bitmap bitmap) {
                Log.e(TAG, "onTakeScreenshot(Bitmap bitmap)");
                mRajawaliBitmap = bitmap;
                mController.takePhoto();
            }
        });
        // 录像时，取Rajawali的帧数据待用
        ((org.rajawali3d.view.SurfaceView) mRenderSurface).setOnTakeScreenshotListener2(new org.rajawali3d.view.SurfaceView.OnTakeScreenshotListener2() {
            @Override
            public void onTakeScreenshot(int[] pixels) {
                Log.e(TAG, "onTakeScreenshot(byte[] pixels)");
                mRajawaliPixels = pixels;
            }
        });

        ((org.rajawali3d.view.SurfaceView) mRenderSurface).getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((org.rajawali3d.view.SurfaceView) mRenderSurface).getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
                mSurfaceWidth = ((org.rajawali3d.view.SurfaceView) mRenderSurface).getWidth();
                mSurfaceHeight = ((org.rajawali3d.view.SurfaceView) mRenderSurface).getHeight();
            }
        });
    }

    private void initCaptureButton() {
        mCapture = (CircularProgressView) findViewById(R.id.btn_capture);
        mCapture.setTotal((int)maxTime);
        // 拍照/录像按钮的逻辑
        mCapture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        recordFlag = false;
                        time = System.currentTimeMillis();
                        mCapture.postDelayed(captureTouchRunnable, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        recordFlag = false;
                        // 短按拍照
                        if(System.currentTimeMillis() - time < 500){
                            mFrameType = TYPE_PHOTO;
                            mCapture.removeCallbacks(captureTouchRunnable);
                            mController.setFrameCallback(IMAGE_WIDTH, IMAGE_HEIGHT, ARCamActivity.this);
                            // 拍照时，先取Rajawali的帧数据
                            ((org.rajawali3d.view.SurfaceView) mRenderSurface).takeScreenshot();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initMenuButton() {
        ImageView ivOrnament = (ImageView) findViewById(R.id.iv_ornament);


        ivOrnament.setColorFilter(Color.WHITE);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.iv_ornament:
                        mOrnamentSheet.show();
                        break;

                }
            }
        };

        ivOrnament.setOnClickListener(onClickListener);

    }

    private void initCommonView() {
        mLayoutRoot = (RelativeLayout) findViewById(R.id.layout_root);
        mTrackText = (TextView) findViewById(R.id.tv_track);
        mActionText = (TextView) findViewById(R.id.tv_action);
    }

    private void initFilterSheet() {
        mFilters = new ArrayList<>();
        mFilterAdapter = new FilterAdapter(mContext, mFilters);
        mFilterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {
                mFilterSheet.dismiss();
                mCurrentFilterId = id;
                setSingleFilter(mController, mCurrentFilterId);
            }
        });

        View sheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_bottom_sheet, null);
        mRvFilter = (RecyclerView) sheetView.findViewById(R.id.rv_gallery);
        mRvFilter.setAdapter(mFilterAdapter);
        mRvFilter.setLayoutManager(new GridLayoutManager(mContext, 4));
        mFilterSheet = new CustomBottomSheet(mContext);
        mFilterSheet.setContentView(sheetView);
        mFilterSheet.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        mFilters.addAll(FilterFactory.getPresetFilter());
        mFilterAdapter.notifyDataSetChanged();
    }

    private void initOrnamentSheet() {
        mOrnaments = new ArrayList<>();
        mOrnamentAdapter = new OrnamentAdapter(mContext, mOrnaments);
        mOrnamentAdapter.setOnItemClickListener(new OrnamentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mOrnamentSheet.dismiss();
                Ornament ornament = mOrnaments.get(position);
                if (position == 0) ornament = null;
                ((My3DRenderer) mISurfaceRenderer).setOrnamentModel(ornament);
                ((My3DRenderer) mISurfaceRenderer).setIsNeedUpdateOrnament(true);
                if (ornament != null) {
                    handleSkinColor(ornament);
                    handleStreamingTexture(ornament);
                    handleFilterInsideOrnament(ornament);

                    String toastMsg = ornament.getToastMsg();
                    if (toastMsg != null) {
                        Toast.makeText(mContext, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        View sheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_bottom_sheet, null);
        mRvOrnament = (RecyclerView) sheetView.findViewById(R.id.rv_gallery);
        mRvOrnament.setAdapter(mOrnamentAdapter);
        mRvOrnament.setLayoutManager(new GridLayoutManager(mContext, 4));
        mOrnamentSheet = new CustomBottomSheet(mContext);
        mOrnamentSheet.setContentView(sheetView);
        mOrnamentSheet.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        mOrnaments.addAll(OrnamentFactory.getPresetOrnament());
        mOrnamentAdapter.notifyDataSetChanged();
    }

    private void handleSkinColor(Ornament ornament) {
        List<Ornament.Model> modelList = ornament.getModelList();
        if (modelList != null && modelList.size() > 0) {
            for (Ornament.Model model : modelList) {
                if (model != null && model.isNeedSkinColor()) {
                    // 获取人脸中心点的颜色
                    mIsNeedSkinColor = true;
                    mController.takePhoto();
                    return;
                }
            }
        }
    }

    private void handleStreamingTexture(Ornament ornament) {
        mIsNeedFrameCallback = false;
        mController.setNeedFrame(false);
        mController.setFrameCallbackType(ornament.getFrameCallbackType());

        mStreamingHandler = null;
        if (mStreamingView != null) {
            mLayoutRoot.removeView(mStreamingView);
            mStreamingView = null;
        }

        List<Ornament.Model> modelList = ornament.getModelList();
        if (modelList != null && modelList.size() > 0) {
            for (Ornament.Model model : modelList) {
                if (model != null && model.isNeedStreaming()) {

                    int streamingViewType = model.getStreamingViewType();
                    if (streamingViewType == Ornament.Model.STREAMING_IMAGE_VIEW) {
                        handleStreamingTextureImageView();
                    } else if (streamingViewType == Ornament.Model.STREAMING_WEB_VIEW) {
                        handleStreamingTextureWebView();
                    }
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            model.getStreamingViewWidth(), model.getStreamingViewHeight());
                    mLayoutRoot.addView(mStreamingView, layoutParams);
                    mStreamingView.setVisibility(View.INVISIBLE);

                    ((My3DRenderer) mISurfaceRenderer).setStreamingView(mStreamingView);

                    if (mStreamingHandler == null) {
                        mStreamingHandler = new Handler(Looper.getMainLooper());
                    }
                    ((My3DRenderer) mISurfaceRenderer).setStreamingHandler(mStreamingHandler);

                    if (ornament.getFrameCallbackType() != TextureController.FRAME_CALLBACK_DISABLE) {
                        mIsNeedFrameCallback = true;
                        mController.setNeedFrame(true);
                    }
                    return;
                }
            }
        }
    }

    private void handleStreamingTextureImageView() {
        mStreamingView = new ImageView(mContext);
        ((ImageView) mStreamingView).setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void handleStreamingTextureWebView() {
        mStreamingView = new WebView(mContext);
        ((WebView) mStreamingView).setWebViewClient(new WebViewClient());
        setDesktopMode(((WebView) mStreamingView), true);
        ((WebView) mStreamingView).setInitialScale(300);
        ((WebView) mStreamingView).loadUrl("https://github.com/SimonCherryGZ/ARCamera");
    }

    private void setDesktopMode(WebView webView, boolean enabled) {
        final WebSettings webSettings = webView.getSettings();

        final String newUserAgent;
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        }
        else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
        }

        webSettings.setUserAgentString(newUserAgent);
        webSettings.setUseWideViewPort(enabled);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setSupportZoom(enabled);
        webSettings.setBuiltInZoomControls(enabled);
    }

    private void handleFilterInsideOrnament(Ornament ornament) {
        if (ornament != null) {
            int selectFilterId = ornament.getSelectFilterId();
            if (selectFilterId != R.id.menu_camera_default) {
                mCurrentFilterId = selectFilterId;
                setSingleFilter(mController, mCurrentFilterId);
            }
        }
    }

    private void initEffectSheet() {
        mEffects = new ArrayList<>();
        mEffectAdapter = new FilterAdapter(mContext, mEffects);
        mEffectAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {
                mEffectSheet.dismiss();
                mCurrentFilterId = id;
                setSingleFilter(mController, mCurrentFilterId);
            }
        });

        View sheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_bottom_sheet, null);
        mRvEffect = (RecyclerView) sheetView.findViewById(R.id.rv_gallery);
        mRvEffect.setAdapter(mEffectAdapter);
        mRvEffect.setLayoutManager(new GridLayoutManager(mContext, 4));
        mEffectSheet = new CustomBottomSheet(mContext);
        mEffectSheet.setContentView(sheetView);
        mEffectSheet.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        mEffects.addAll(FilterFactory.getPresetEffect());
        mEffectAdapter.notifyDataSetChanged();
    }

    private void initMaskSheet() {
        mMasks = new ArrayList<>();
        mMaskAdapter = new PhotoAdapter(mContext, mMasks);
        mMaskAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path) {
                mMaskSheet.dismiss();
                //Toast.makeText(mContext, path, Toast.LENGTH_SHORT).show();
                if (tracker == null) {
                    tracker = new STMobileMultiTrack106(mContext, ST_MOBILE_TRACKING_ENABLE_FACE_ACTION);
                    int max = 1;
                    tracker.setMaxDetectableFaces(max);
                }
                boolean isSuccess = LandmarkUtils.replaceTexture(mContext, tracker, path);
                Toast.makeText(mContext, "isSuccess: " + isSuccess, Toast.LENGTH_SHORT).show();
                if (isSuccess) {
                    Ornament ornament = OrnamentFactory.getMask(path);
                    ((My3DRenderer) mISurfaceRenderer).setOrnamentModel(ornament);
                    ((My3DRenderer) mISurfaceRenderer).setIsNeedUpdateOrnament(true);
                    // 获取人脸中心点的颜色
                    mIsNeedSkinColor = true;
                    mController.takePhoto();
                }
            }
        });

        View sheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_bottom_sheet, null);
        mRvMask = (RecyclerView) sheetView.findViewById(R.id.rv_gallery);
        mRvMask.setAdapter(mMaskAdapter);
        mRvMask.setLayoutManager(new GridLayoutManager(mContext, 4));
        mMaskSheet = new CustomBottomSheet(mContext);
        mMaskSheet.setContentView(sheetView);
        mMaskSheet.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        loadLocalImage();
    }

    // 初始化的Runnable
    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {
            mExecutor = Executors.newSingleThreadExecutor();
            mController = new TextureController(mContext);
            // 设置数据源
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRenderer = new CameraTrackRenderer(mContext, (CameraManager)getSystemService(CAMERA_SERVICE), mController, cameraId);
                // 人脸检测的回调
                ((CameraTrackRenderer) mRenderer).setTrackCallBackListener(new CameraTrackRenderer.TrackCallBackListener() {
                    @Override
                    public void onTrackDetected(STMobileFaceAction[] faceActions, final int orientation, final int value,
                                                final float pitch, final float roll, final float yaw,
                                                final int eye_dist, final int id, final int eyeBlink, final int mouthAh,
                                                final int headYaw, final int headPitch, final int browJump) {
                        onTrackDetectedCallback(faceActions, orientation, value,
                                pitch, roll, yaw, eye_dist, id, eyeBlink, mouthAh, headYaw, headPitch, browJump);
                    }
                });

            }else{
                // Camera1暂时没写人脸检测
                mRenderer = new Camera1Renderer(mController, cameraId);
            }

            setContentView();

            mController.setFrameCallback(IMAGE_WIDTH, IMAGE_HEIGHT, ARCamActivity.this);
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mController.surfaceCreated(holder);
                    mController.setRenderer(mRenderer);
                }
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mController.surfaceChanged(width, height);
                }
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mController.surfaceDestroyed();
                }
            });
        }
    };

    //录像的Runnable
    private Runnable captureTouchRunnable = new Runnable() {
        @Override
        public void run() {
            recordFlag = true;
            mExecutor.execute(recordRunnable);
        }
    };

    private Runnable recordRunnable = new Runnable() {

        @Override
        public void run() {
            mFrameType = TYPE_RECORD;
            long timeCount = 0;
            if(mp4Recorder == null){
                mp4Recorder = new CameraRecorder();
            }
            long time = System.currentTimeMillis();
            String savePath = FileUtils.getPath(getApplicationContext(), "video/", time + ".mp4");
            mp4Recorder.setSavePath(FileUtils.getPath(getApplicationContext(), "video/", time+""), "mp4");
            try {
                mp4Recorder.prepare(VIDEO_WIDTH, VIDEO_HEIGHT);
                mp4Recorder.start();
                mController.setFrameCallback(VIDEO_WIDTH, VIDEO_HEIGHT, ARCamActivity.this);
                mController.startRecord();
                ((org.rajawali3d.view.SurfaceView) mRenderSurface).startRecord();

                while (timeCount <= maxTime && recordFlag){
                    long start = System.currentTimeMillis();
                    mCapture.setProcess((int)timeCount);
                    long end = System.currentTimeMillis();
                    try {
                        Thread.sleep(timeStep - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeCount += timeStep;
                }
                mController.stopRecord();
                ((org.rajawali3d.view.SurfaceView) mRenderSurface).stopRecord();
                mFrameType = TYPE_NONE;

                if(timeCount < 2000){
                    mp4Recorder.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCapture.setProcess(0);
                            Toast.makeText(mContext, "录像时间太短了", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    mp4Recorder.stop();
                    recordComplete(mFrameType, savePath);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private void recordComplete(int type, final String path){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
                Toast.makeText(mContext,"文件保存路径："+path,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ARCamActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCurrentFilterId = item.getItemId();
        if (mCurrentFilterId == R.id.menu_camera_switch) {
            switchCamera();
        } else {
            setSingleFilter(mController, mCurrentFilterId);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSingleFilter(TextureController controller, int menuId) {
        controller.clearFilter();
        controller.addFilter(FilterFactory.getFilter(getResources(), menuId));
    }

    public void switchCamera(){
        cameraId = cameraId == 1 ? 0 : 1;
        if (mController != null) {
            mController.destroy();
        }
        initViewRunnable.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mController != null) {
            mController.onResume();
            mController.setNeedFrame(mIsNeedFrameCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mController != null) {
            mController.setNeedFrame(false);
            mController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.setNeedFrame(false);
            mController.destroy();
        }
    }

    @Override
    public void onFrame(final byte[] bytes, long time) {
        // 录像有问题，暂时跳过
        if (mIsNeedFrameCallback && mStreamingView != null && mFrameType != TYPE_RECORD) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int bitmapWidth = mController.getFrameCallbackWidth();
                    int bitmapHeight = mController.getFrameCallbackHeight();
                    Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
                            Bitmap.Config.ARGB_8888);
                    ByteBuffer b = ByteBuffer.wrap(bytes);
                    bitmap.copyPixelsFromBuffer(b);
                    if (mStreamingView != null && mStreamingView instanceof ImageView) {
                        ((ImageView) mStreamingView).setImageBitmap(bitmap);
                    }
                }
            });
        }

        if (mIsNeedSkinColor) {  // 获取人脸中心点的颜色
            Log.e(TAG, "isNeedSkinColor");
            if (mSamplePoint != null) {
                Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT,
                        Bitmap.Config.ARGB_8888);
                ByteBuffer b = ByteBuffer.wrap(bytes);
                // FIXME -- java.lang.RuntimeException: Buffer not large enough for pixels
                bitmap.copyPixelsFromBuffer(b);

                int x = (int) mSamplePoint.x;
                int y = (int) mSamplePoint.y;
                Log.e(TAG, "points[44]: x= " + x + ", y= " + y);
                int pixel = bitmap.getPixel(x, y);
                String skinColor = Integer.toHexString(pixel);
                Log.e(TAG, "get Skin Color: " + skinColor);
                bitmap.recycle();
                mSamplePoint = null;
                mIsNeedSkinColor = false;
                // 根据肤色更改模型贴图的颜色
                ((My3DRenderer) mISurfaceRenderer).setSkinColor(pixel);
            }

        } else if (mp4Recorder != null && mFrameType == TYPE_RECORD) {  // 处理录像
            handleVideoFrame(bytes);
        } else if (mFrameType == TYPE_PHOTO) {  // 处理拍照
            mFrameType = TYPE_NONE;
            handlePhotoFrame(bytes);
        }
    }

    @Override
    public void onSavePhotoSuccess(final String fileName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ARCamActivity.this, "保存成功->" + fileName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSavePhotoFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ARCamActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGetVideoData(byte[] bytes) {
        mp4Recorder.feedData(bytes, time);
    }

    @Override
    public void onGet3dModelRotation(float pitch, float roll, float yaw) {
        ((My3DRenderer) mISurfaceRenderer).setAccelerometerValues(roll, yaw, pitch);

        if (mStreamingView != null && mStreamingView instanceof WebView) {
            if (!mStreamingView.canScrollHorizontally(-1) && yaw > 0) {
                yaw = 0;
            }

            if (!mStreamingView.canScrollHorizontally(1) && yaw < 0) {
                yaw = 0;
            }

            if (!mStreamingView.canScrollVertically(-1) && pitch > 0) {
                pitch = 0;
            }

            if (!mStreamingView.canScrollVertically(1) && pitch < 0) {
                pitch = 0;
            }

            mStreamingView.scrollBy((int) (-yaw * 3), (int) (-pitch * 3));
        }
    }

    @Override
    public void onGet3dModelTransition(float x, float y, float z) {
        ((My3DRenderer) mISurfaceRenderer).setTransition(x, y, z);
    }

    @Override
    public void onGetFaceLandmark(float[] landmarkX, float[] landmarkY, int isMouthOpen) {
        if (mIsNeedSkinColor) {
            float x = landmarkX[44] * IMAGE_WIDTH;
            float y = landmarkY[44] * IMAGE_HEIGHT;
            mSamplePoint = new PointF(x, y);
        }

        AFilter aFilter = mController.getLastFilter();
        if(aFilter != null && aFilter instanceof LandmarkFilter) {
            ((LandmarkFilter) aFilter).setLandmarks(landmarkX, landmarkY);
            ((LandmarkFilter) aFilter).setMouthOpen(isMouthOpen);
        }

        float[] copyLandmarkX = new float[landmarkX.length];
        float[] copyLandmarkY = new float[landmarkY.length];
        System.arraycopy(landmarkX, 0, copyLandmarkX, 0, landmarkX.length);
        System.arraycopy(landmarkY, 0, copyLandmarkY, 0, landmarkY.length);
        mPresenter.handleChangeModel(copyLandmarkX, copyLandmarkY);
    }

    @Override
    public void onGetChangePoint(List<DynamicPoint> mDynamicPoints) {
        ((My3DRenderer) mISurfaceRenderer).setDynamicPoints(mDynamicPoints);
    }

    private void handleVideoFrame(final byte[] bytes) {
        mPresenter.handleVideoFrame(bytes, mRajawaliPixels);
    }

    private void handlePhotoFrame(final byte[] bytes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.handlePhotoFrame(bytes, mRajawaliBitmap, IMAGE_WIDTH, IMAGE_HEIGHT);
            }
        }).start();
    }

    private void onTrackDetectedCallback(STMobileFaceAction[] faceActions, final int orientation, final int value,
                                         final float pitch, final float roll, final float yaw,
                                         final int eye_dist, final int id, final int eyeBlink, final int mouthAh,
                                         final int headYaw, final int headPitch, final int browJump) {
        // 处理3D模型的旋转
        mPresenter.handle3dModelRotation(pitch, roll, yaw);
        // 处理3D模型的平移
        mPresenter.handle3dModelTransition(faceActions, orientation, eye_dist, yaw, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        // 处理人脸关键点
        mPresenter.handleFaceLandmark(faceActions, orientation, mouthAh, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        // 显示人脸检测的参数
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrackText.setText("TRACK: " + value + " MS"
                        + "\nPITCH: " + pitch + "\nROLL: " + roll + "\nYAW: " + yaw + "\nEYE_DIST:" + eye_dist);
                mActionText.setText("ID:" + id + "\nEYE_BLINK:" + eyeBlink + "\nMOUTH_AH:"
                        + mouthAh + "\nHEAD_YAW:" + headYaw + "\nHEAD_PITCH:" + headPitch + "\nBROW_JUMP:" + browJump);
            }
        });
    }

    private void loadLocalImage() {
        mediaLoaderCallback = new MediaLoaderCallback(mContext);
        mediaLoaderCallback.setOnLoadFinishedListener(new MediaLoaderCallback.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(List<Photo> data) {
                //Toast.makeText(mContext, "Total Size: " + data.size(), Toast.LENGTH_SHORT).show();
                mMasks.addAll(data);
                mMaskAdapter.notifyDataSetChanged();
            }
        });
        getSupportLoaderManager().initLoader(0, null, mediaLoaderCallback);
    }
}
