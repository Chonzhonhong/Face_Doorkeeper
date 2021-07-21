package com.seeta.sdk;

/**
 * 人脸识别
 */
public class FaceRecognizer {

    static {
        System.loadLibrary("SeetaFaceRecognizer600_java");
    }


    public long impl = 0;

    private native void construct(SeetaModelSetting setting);
    public FaceRecognizer(SeetaModelSetting setting) {
        this.construct(setting);
    }

    public native void dispose();
    protected void finalize()throws Throwable {
        super.finalize();
        this.dispose();
    }

    public native int GetCropFaceWidth();
    public native int GetCropFaceHeight();
    public native int GetCropFaceChannels();

    //获取提取特征尺寸
    public native int GetExtractFeatureSize();

    public native boolean CropFace(SeetaImageData image, SeetaPointF[] points, SeetaImageData face);

    public native boolean ExtractCroppedFace(SeetaImageData face, float[] features);

    public native boolean Extract(SeetaImageData image, SeetaPointF[] points, float[] features);

    //计算两个特征值相似度
    public native float CalculateSimilarity(float[] features1, float[] features2);
}
