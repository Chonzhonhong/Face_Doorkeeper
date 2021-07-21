package com.lyn.face_doorkeeper.ui.fragment;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.lyn.face_doorkeeper.databinding.FragmentFaceDetectorBinding;
import com.lyn.face_doorkeeper.ui.App;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.lyn.face_doorkeeper.utils.YuvUtils;
import com.seeta.sdk.Nv21Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class FaceDetectorFragment extends Fragment implements Camera.PreviewCallback {
    private FragmentFaceDetectorBinding binding;
    private SurfaceHolder faceDetectorHolder;
    private Camera camera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFaceDetectorBinding.inflate(getLayoutInflater());
        binding.faceDetectorSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        faceDetectorHolder = binding.faceDetectorSurface.getHolder();
        faceDetectorHolder.addCallback(faceDetectorHolderCallback);
        return binding.getRoot();
    }


    /**
     * 人脸检测SurfaceHolder回调
     */
    private SurfaceHolder.Callback faceDetectorHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            openCamera();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            stopPreviewAndFreeCamera();
        }
    };


    /**
     * 打开摄像头
     */
    private void openCamera() {
        if (Camera.getNumberOfCameras() > 0) {
            try {
                camera = Camera.open(1);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(640, 480);
                camera.setParameters(parameters);
                Camera.Size size = parameters.getPreviewSize();
                camera.addCallbackBuffer(new byte[size.width * size.height * 12 / 8]);
                camera.setPreviewCallbackWithBuffer(this);
                camera.setPreviewDisplay(faceDetectorHolder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                getCameraResolution(camera);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭摄像头
     */
    private void stopPreviewAndFreeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private void getCameraResolution(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizes) {
            LogUtils.i("支持分辨率:" + JSON.toJSONString(size));
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(data);
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        Nv21Data nv21Data = new Nv21Data();
        nv21Data.setData(data);
        nv21Data.setWidth(size.width);
        nv21Data.setHeight(size.height);
        if (callback != null) {
            callback.toNv21Data(nv21Data);
        }

    }


    //对焦
    public void Focus() {
        if (camera != null) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void toNv21Data(Nv21Data nv21Data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
