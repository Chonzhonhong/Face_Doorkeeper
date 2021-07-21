package com.lyn.face_doorkeeper.entity;

import java.util.Arrays;
import java.util.Objects;

/**
 * 人脸特征和人员id绑定
 */
public class FaceInfo {
    private long id;

    private float[] feature;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaceInfo faceInfo = (FaceInfo) o;
        return id == faceInfo.id &&
                Arrays.equals(feature, faceInfo.feature);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(feature);
        return result;
    }
}
