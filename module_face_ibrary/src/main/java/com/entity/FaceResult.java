package com.entity;


import android.graphics.Bitmap;
import android.graphics.RectF;

import org.opencv.core.Rect;

public class FaceResult {

    private Rect rect;

    private RectF rectF;

    private Bitmap bitmap;

    private float[] feature;

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }
}
