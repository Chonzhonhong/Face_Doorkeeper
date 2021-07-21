package com.mvp;

import com.config.AppConfig;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaRect;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class PresenterImpl {
    static {
        System.loadLibrary("opencv_java3");
    }

    public static class TrackingInfo {
        public Mat matBgr;
        public Mat matGray;
        public SeetaRect faceInfo = new SeetaRect();
        public Rect faceRect = new Rect();
        public android.graphics.RectF faceRectF = new android.graphics.RectF();
        public long birthTime;
        public long lastProccessTime;
        public boolean isFace;
    }


    public static Mat matNv21 = new Mat(AppConfig.CAMERA_PREVIEW_HEIGHT + AppConfig.CAMERA_PREVIEW_HEIGHT / 2,
            AppConfig.CAMERA_PREVIEW_WIDTH, CvType.CV_8UC1);



    private static int WIDTH = AppConfig.IMAGE_WIDTH;
    private static int HEIGHT = AppConfig.IMAGE_HEIGHT;
    public static SeetaImageData imageData = new SeetaImageData(WIDTH, HEIGHT, 3);

}
