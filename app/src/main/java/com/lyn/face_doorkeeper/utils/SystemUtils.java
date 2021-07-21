package com.lyn.face_doorkeeper.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SystemUtils {

    private static class SystemUtilsTypeClass {
        private static SystemUtils instance = new SystemUtils();
    }

    public static SystemUtils getInstance() {
        return SystemUtilsTypeClass.instance;
    }

    public   int[] getAndroidScreenProperty(Context context) {
        int[] screen = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        screen[0] = screenWidth;
        screen[1] = screenHeight;

        return screen;
    }
}
