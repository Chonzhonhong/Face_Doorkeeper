package com.face;

import android.content.Context;

import cn.heils.a.c;
import cn.heils.detect.FaceCnn;

/**
 * 活体算法帮助类
 */
public class LivingBodyHelper {

    private static class LivingBodyHelperTypeClass {
        private static LivingBodyHelper instance = new LivingBodyHelper();
    }

    public static LivingBodyHelper getInstance() {
        return LivingBodyHelperTypeClass.instance;
    }

    private static FaceCnn faceCnn = new FaceCnn();

    public void init(Context context) {
        faceCnn.a(context);
        c.a(context);
    }


    private volatile Object nv21DetectObject = new Object();
    private volatile float[] var7 = new float[110592];

    public float nv21Detect(byte[] var1, int var2, int var3, float var4, int var5, boolean var6) {
        synchronized (nv21DetectObject) {
            var7.clone();
            int[] var8 = faceCnn.a().nv21Detect(var1, var2, var3, var7, var4, var5, var6);
            if (var8.length != 0 && var8.length != 1) {
                float[] var10 = c.b(var7);
                float var11 = c.c(var10) / 16384.0F;
                return var11;
            } else {
                return 0f;
            }
        }
    }

    public int getGate() {
        return faceCnn.getGate();
    }
}
