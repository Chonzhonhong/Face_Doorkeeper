package com.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.config.AppConfig;
import com.entity.FaceResult;
import com.mvp.PresenterImpl;
import com.seeta.sdk.FaceDetector;
import com.seeta.sdk.FaceLandmarker;
import com.seeta.sdk.FaceRecognizer;
import com.seeta.sdk.SeetaDevice;
import com.seeta.sdk.SeetaModelSetting;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import com.utils.BitmapToNv21;
import com.utils.FileUtils;
import com.utils.NV21ToBitmap;
import com.utils.Nv21Spin;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/**
 * 人脸识别帮助类
 */
public class FaceHelper {
    private static final String TAG="FaceHelper";
    private FaceDetector faceDetector;
    private FaceLandmarker faceLandmarker;
    private FaceRecognizer faceRecognizer;
    private PresenterImpl presenter;
    private NV21ToBitmap nv21ToBitmap;

    private static class FaceHelperTypeClass {
        private static FaceHelper instance = new FaceHelper();
    }

    public static FaceHelper getInstance() {
        return FaceHelperTypeClass.instance;
    }



    /**
     * 初始化算法
     *
     * @param context
     */
    public void init(Context context) throws Exception {
        File FilesDir = getInternalFilesDirectory(context, null);
        String modelPath = FilesDir.getAbsolutePath();

        String fdModel = "face_detector.csta";
        String pdModel = "face_landmarker_pts5.csta";
        String frModel = "face_recognizer.csta";

        if (!isExists(modelPath, fdModel)) {
            File fdFile = new File(FilesDir + "/" + fdModel);
            FileUtils.copyFromAsset(context, fdModel, fdFile, false);
        }
        if (!isExists(modelPath, pdModel)) {
            File pdFile = new File(FilesDir + "/" + pdModel);
            FileUtils.copyFromAsset(context, pdModel, pdFile, false);
        }
        if (!isExists(modelPath, frModel)) {
            File frFile = new File(FilesDir + "/" + frModel);
            FileUtils.copyFromAsset(context, frModel, frFile, false);
        }

        String rootPath = FilesDir + "/";

        if (presenter == null || faceDetector == null || faceLandmarker == null || faceRecognizer == null) {
            presenter = new PresenterImpl();

            faceDetector = new FaceDetector(new SeetaModelSetting(0, new String[]{rootPath + fdModel}, SeetaDevice.SEETA_DEVICE_AUTO));

            faceLandmarker = new FaceLandmarker(new SeetaModelSetting(0, new String[]{rootPath + pdModel}, SeetaDevice.SEETA_DEVICE_AUTO));

            faceRecognizer = new FaceRecognizer(new SeetaModelSetting(0, new String[]{rootPath + frModel}, SeetaDevice.SEETA_DEVICE_AUTO));
        }
        faceDetector.set(FaceDetector.Property.PROPERTY_MIN_FACE_SIZE, AppConfig.MIN_FACE_SIZE);

        nv21ToBitmap=new NV21ToBitmap(context);
    }

    /**
     * 验证文件是否存在
     *
     * @param path
     * @param modelName
     * @return
     */
    public boolean isExists(String path, String modelName) {
        File file = new File(path + "/" + modelName);
        if (file.exists()) return true;
        return false;
    }

    /**
     * 获取设备包名文件目录
     *
     * @param context
     * @param type
     * @return
     */
    public File getInternalFilesDirectory(Context context, String type) {
        File appFilesDir = null;
        if (TextUtils.isEmpty(type)) {
            appFilesDir = context.getFilesDir();// /data/data/app_package_name/cache
        } else {
            appFilesDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }

        if (!appFilesDir.exists() && !appFilesDir.mkdirs()) {
            Log.e("getInternalDirectory", "getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appFilesDir;
    }


    private float PreviewScaleX = 1.0f;
    private float PreviewScaleY = 1.0f;

    public float getPreviewScaleX() {
        return PreviewScaleX;
    }

    public float getPreviewScaleY() {
        return PreviewScaleY;
    }

    /**
     * 设置预览检测框X比例
     *
     * @param PreviewScaleX
     */
    public void setPreviewScaleX(float PreviewScaleX) {
        this.PreviewScaleX = PreviewScaleX;
    }

    public void setPreviewScaleY(float PreviewScaleY) {
        this.PreviewScaleY = PreviewScaleY;
    }



    /**
     * 人脸检测
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public PresenterImpl.TrackingInfo detect(byte[] data, int width, int height) {
        if (data == null) {
            Log.i(TAG,"data == null");
            return null;
        }
        PresenterImpl.TrackingInfo trackingInfo = new PresenterImpl.TrackingInfo();

        presenter.matNv21.put(0, 0, data);
        trackingInfo.matBgr = new Mat(height, width, CvType.CV_8UC3);
        trackingInfo.matGray = new Mat();
        Imgproc.cvtColor(presenter.matNv21, trackingInfo.matBgr, Imgproc.COLOR_YUV2BGR_NV21);
        Core.transpose(trackingInfo.matBgr, trackingInfo.matBgr);
        Core.flip(trackingInfo.matBgr, trackingInfo.matBgr, 0);
        Core.flip(trackingInfo.matBgr, trackingInfo.matBgr, 1);

        Imgproc.cvtColor(trackingInfo.matBgr, trackingInfo.matGray, Imgproc.COLOR_BGR2GRAY);
        //开始检测的时间
        trackingInfo.birthTime = System.currentTimeMillis();
        trackingInfo.lastProccessTime = System.currentTimeMillis();


        trackingInfo.matBgr.get(0, 0, presenter.imageData.data);
        SeetaRect[] faces = faceDetector.Detect(presenter.imageData);
        trackingInfo.faceInfo.x =  0;
        trackingInfo.faceInfo.y = 0;
        trackingInfo.faceInfo.width = 0;
        trackingInfo.faceInfo.height = 0;
        trackingInfo.isFace = false;
        if (faces.length != 0) {
            int maxIndex = 0;
            double maxWidth = 0;
            for (int i = 0; i < faces.length; ++i) {
                if (faces[i].width > maxWidth) {
                    maxIndex = i;
                    maxWidth = faces[i].width;
                }
            }
            trackingInfo.faceInfo = faces[maxIndex];
            trackingInfo.faceRect.x = faces[maxIndex].x;
            trackingInfo.faceRect.y = faces[maxIndex].y;
            trackingInfo.faceRect.width = faces[maxIndex].width;
            trackingInfo.faceRect.height = faces[maxIndex].height;
            if (trackingInfo.faceRect != null) {
                trackingInfo.faceRectF.left = trackingInfo.faceRect.x * PreviewScaleX;
                trackingInfo.faceRectF.right = trackingInfo.faceRect.x * PreviewScaleX + trackingInfo.faceRect.width * PreviewScaleX;
                trackingInfo.faceRectF.top = trackingInfo.faceRect.y * PreviewScaleY;
                trackingInfo.faceRectF.bottom = trackingInfo.faceRect.y * PreviewScaleY + trackingInfo.faceRect.height * PreviewScaleY;
                //结束检测的时间
                trackingInfo.lastProccessTime = System.currentTimeMillis();
                trackingInfo.isFace = true;
                return trackingInfo;
            }
        }
        return trackingInfo;
    }


    /**
     * 提取特征值
     *
     * @param trackingInfo
     * @return
     */
    public float[] getFeature(PresenterImpl.TrackingInfo trackingInfo) {
        trackingInfo.matGray = new Mat();
        trackingInfo.matBgr.get(0, 0, presenter.imageData.data);
        float[] feats = new float[faceRecognizer.GetExtractFeatureSize()];
        if (trackingInfo.faceInfo.width != 0) {
            //特征点检测
            SeetaPointF[] points = new SeetaPointF[5];
            faceLandmarker.mark(presenter.imageData, trackingInfo.faceInfo, points);

            //特征提取
            boolean extract = faceRecognizer.Extract(presenter.imageData, points, feats);
            if (extract) {
                return feats;
            }
        }

        return null;
    }

    /**
     * 提取bitmap人脸照片特征值
     *
     * @param bitmap
     * @return
     */
    public FaceResult ExtractBitmapFaceInfo(Bitmap bitmap,boolean isCrop) {
        if (bitmap == null) {
            Log.i(TAG,"bitmap == null");
            return null;
        }

        byte[] nv21 = BitmapToNv21.bitmapToNv21(bitmap);
        if (nv21 == null) {
            Log.i(TAG,"nv21 == null");
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        PresenterImpl.TrackingInfo trackingInfo = detect(Nv21Spin.rotateYUV420Degree90(nv21,width,height), width, height);
        if (trackingInfo == null) {
            Log.i(TAG,"trackingInfo == null");
            return null;
        }
        FaceResult faceResult = new FaceResult();
        if (trackingInfo.isFace) {
            float[] feature = getFeature(trackingInfo);
            if (feature == null) {
                Log.i(TAG,"feature == null");
                return null;
            }
            Bitmap extractBitmap = MatToBitmap(trackingInfo,isCrop);
            if (extractBitmap == null) {
                Log.i(TAG,"extractBitmap == null");
                return null;
            }
            faceResult.setRect(trackingInfo.faceRect);
            faceResult.setRectF(trackingInfo.faceRectF);
            faceResult.setFeature(feature);
            faceResult.setBitmap(extractBitmap);
            return faceResult;
        }
        return null;
    }

    /**
     * 提取nv21Data人脸照片特征值
     *
     * @param nv21Data
     * @return
     */
    public FaceResult ExtractBitmapFaceInfo(byte[] nv21Data,int width,int height,boolean isCrop) {

        PresenterImpl.TrackingInfo trackingInfo = detect(nv21Data, width, height);
        if (trackingInfo == null) {
            Log.i(TAG,"trackingInfo == null");
            return null;
        }
        FaceResult faceResult = new FaceResult();
        if (trackingInfo.isFace) {
            float[] feature = getFeature(trackingInfo);
            if (feature == null) {
                Log.i(TAG,"feature == null");
                return null;
            }

            Bitmap extractBitmap = MatToBitmap(trackingInfo,isCrop);
            if (extractBitmap == null) {
                Log.i(TAG,"extractBitmap == null");
                return null;
            }
            faceResult.setRect(trackingInfo.faceRect);
            faceResult.setRectF(trackingInfo.faceRectF);
            faceResult.setFeature(feature);
            faceResult.setBitmap(extractBitmap);
            return faceResult;
        }
        return null;
    }

    /**
     * 两个特征值比对
     *
     * @param features1
     * @param features2
     * @return 相似度
     */
    public float FeatureComparison(float[] features1, float[] features2) {
        if (features1 == null || features2 == null) {
            return 0;
        }
        float sim = faceRecognizer.CalculateSimilarity(features1, features2);
        return sim;
    }



    /**
     * 提取bitmap
     *
     * @return
     */
    public Bitmap MatToBitmap(PresenterImpl.TrackingInfo trackingInfo, boolean isCrop) {
        if (trackingInfo==null) {
            return null;
        }
        int limitX = trackingInfo.faceRect.x + trackingInfo.faceRect.width;
        int limitY = trackingInfo.faceRect.y + trackingInfo.faceRect.height;
        if (limitX < 640 && limitY < 480) {
            Rect rect=new Rect();
            rect.x=0;
            rect.y=0;
            rect.width=480;
            rect.height=640;
            Mat faceMatBGR = new Mat(trackingInfo.matBgr,isCrop?trackingInfo.faceRect:rect);
            Imgproc.resize(faceMatBGR, faceMatBGR, new Size(480, 640));
            Mat faceMatBGRA = new Mat();
            Imgproc.cvtColor(faceMatBGR, faceMatBGRA, Imgproc.COLOR_BGR2RGBA);
            Bitmap faceBmp = Bitmap.createBitmap(faceMatBGR.width(), faceMatBGR.height(),
                    Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(faceMatBGRA, faceBmp);
            return faceBmp;
        }
        return null;
    }
}



