package com.lyn.face_doorkeeper.utils;

import android.content.Context;

import com.tencent.mmkv.MMKV;

public class MMKVUtils {
    private static class MMKVUtilsTypeClass {
        private static MMKVUtils instance = new MMKVUtils();
    }

    public static MMKVUtils getInstance() {
        return MMKVUtilsTypeClass.instance;
    }

    private MMKV mmkv;

    public void init(Context context) {
        String initialize = MMKV.initialize(context);
        LogUtils.i("MMKV初始化:" + initialize);
        mmkv = MMKV.defaultMMKV();
    }

    public boolean setString(String key, String values) {
        return mmkv.encode(key, values);
    }

    public String getString(String key, String defValue) {
        return mmkv.getString(key, defValue);
    }

    public boolean setBoolean(String key, boolean values) {
        return mmkv.encode(key, values);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mmkv.getBoolean(key, defValue);
    }

    public boolean setInt(String key, int values) {
        return mmkv.encode(key, values);
    }

    public int getInt(String key, int defValue) {
        return mmkv.getInt(key, defValue);
    }

    //人脸识别阈值
    public static final String FACE_RECOGNITION_THRESHOLD = "faceRecognitionThreshold";
    //活体
    public static final String LIVING_BODY = "livingBody";
    //同人识别间隔
    public static final String FAN_RECOGNITION_INTERVAL = "fanRecognitionInterval";
    //验证实名显示
    public static final String VERIFY_REAL_NAME_DISPLAY="verifyRealNameDisplay";


    public class defValue {
        //人脸识别阈值默认值
        public static final int FACE_RECOGNITION_THRESHOLD = 60;
        //活体
        public static final boolean LIVING_BODY = false;
        //同人识别间隔
        public static final int FAN_RECOGNITION_INTERVAL = 2;
        //验证实名显示
        public static final boolean VERIFY_REAL_NAME_DISPLAY=false;
    }
}
