package com.lyn.face_doorkeeper.entity;

import android.hardware.Camera;

public class CameraInfo {
    private byte[] data;
    private Camera camera;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
