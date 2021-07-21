package com.lyn.face_doorkeeper.ui.activity;

import android.content.Intent;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.face.FaceHelper;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.ActivityTakePicturesBinding;
import com.lyn.face_doorkeeper.entity.CameraInfo;
import com.lyn.face_doorkeeper.ui.App;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.lyn.face_doorkeeper.utils.SystemUtils;
import com.lyn.face_doorkeeper.utils.ToneUtil;
import com.mvp.PresenterImpl;

public class TakePicturesActivity extends BaseActivity<ActivityTakePicturesBinding> implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder holder;
    private Camera camera;
    private volatile byte[] data;

    private final HandlerThread handlerThread;

    {
        handlerThread = new HandlerThread(this.getClass().getName(), Process.THREAD_PRIORITY_MORE_FAVORABLE);
        handlerThread.start();
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityTakePicturesBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityTakePicturesBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_takePictures));
        holder = bindView.Preview.getHolder();
        holder.addCallback(this);
    }

    @Override
    protected void initListener() {
        bindView.Toolbar.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            camera = Camera.open(1);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(640, 480);
            camera.setParameters(parameters);
            Camera.Size size = parameters.getPreviewSize();
            if (data == null) {
                data = new byte[size.width * size.height * 12 / 8];
            }
            camera.addCallbackBuffer(data);
            camera.setPreviewCallbackWithBuffer(this);
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private CameraInfo cameraInfo = new CameraInfo();

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(data);
        handler.removeMessages(1);
        cameraInfo.setData(data);
        cameraInfo.setCamera(camera);
        if (isFinishing()) {
            return;
        }
        handler.obtainMessage(1, cameraInfo).sendToTarget();
    }

    private volatile int countFace;
    private volatile long countTime;
    private volatile int TotalDetection = 5;

    private Handler handler = new Handler(handlerThread.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            CameraInfo cameraInfo = (CameraInfo) msg.obj;
            Camera.Parameters parameters = cameraInfo.getCamera().getParameters();
            Camera.Size size = parameters.getPreviewSize();
            PresenterImpl.TrackingInfo trackingInfo = FaceHelper.getInstance().detect(cameraInfo.getData(), size.width, size.height);
            if (trackingInfo == null) {
                bindView.FaceTracking.setRectF(null);
                bindView.FaceTracking.setText(null);
                countFace = 0;
                return;
            }
            if (!trackingInfo.isFace) {
                bindView.FaceTracking.setRectF(null);
                bindView.FaceTracking.setText(null);
                countFace = 0;
                return;
            }
            bindView.FaceTracking.setRectF(trackingInfo.faceRectF);
            RectF faceRectF = trackingInfo.faceRectF;
            float deviation = 20;
            float widthFaceCenter = (faceRectF.right / FaceHelper.getInstance().getPreviewScaleX() - faceRectF.left / FaceHelper.getInstance().getPreviewScaleX()) / 2 + faceRectF.left / FaceHelper.getInstance().getPreviewScaleX();
            float widthCameraCenter = size.height / 2;
            float widthOffset = Math.abs(widthFaceCenter - widthCameraCenter);

            float heightFaceCenter = (faceRectF.bottom / FaceHelper.getInstance().getPreviewScaleY() - faceRectF.top / FaceHelper.getInstance().getPreviewScaleY()) / 2 + faceRectF.top / FaceHelper.getInstance().getPreviewScaleY();
            float heightCameraCenter = size.width / 2;
            float heightOffset = Math.abs(heightFaceCenter - heightCameraCenter);
            LogUtils.i("偏移中心距离:" + widthOffset);
            if (widthOffset > deviation && heightOffset > deviation) {
                countFace = 0;
                bindView.FaceTracking.setText(null);
                return;
            }
            if (System.currentTimeMillis() - countTime >= 500) {
                countTime = System.currentTimeMillis();
                countFace++;
                ToneUtil.playPromptTone(countFace);
                bindView.FaceTracking.setText(getString(R.string.str_detected) + countFace + getString(R.string.str_totalDetection) + TotalDetection);
            }
            if (countFace < TotalDetection) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("width", size.width);
            intent.putExtra("height", size.height);
            intent.putExtra("data", data);
            setResult(1, intent);
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            handlerThread.quitSafely();
        }
    }
}
