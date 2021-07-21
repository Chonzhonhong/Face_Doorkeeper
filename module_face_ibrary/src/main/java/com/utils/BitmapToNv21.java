package com.utils;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapToNv21 {
    private static final String TAG = "BitmapToNv21";

    /**
     * Bitmap转化为ARGB数据，再转化为NV21数据
     *
     * @param src    传入ARGB_8888的Bitmap
     * @return nv21数据
     */
    public static byte[] bitmapToNv21(Bitmap src) {
        if (src==null){
            return null;
        }
        Bitmap bitmap=src.copy(Bitmap.Config.ARGB_8888,true);
        int width = bitmap.getWidth() % 2 == 0 ? bitmap.getWidth() : bitmap.getWidth() - 1;
        int height = bitmap.getHeight() % 2 == 0 ? bitmap.getHeight() : bitmap.getHeight() - 1;
        if (width != bitmap.getWidth() || height != bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }
        int[] argb = new int[width * height];
        bitmap.getPixels(argb, 0, width, 0, 0, width, height);
        return argbToNv21(argb, width, height);
    }

    /**
     * ARGB数据转化为NV21数据
     *
     * @param argb   argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    private static byte[] argbToNv21(int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int R = (argb[index] & 0xFF0000) >> 16;
                int G = (argb[index] & 0x00FF00) >> 8;
                int B = argb[index] & 0x0000FF;
                int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                nv21[yIndex++] = (byte) (Y < 0 ? 0 : (Y > 255 ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (V < 0 ? 0 : (V > 255 ? 255 : V));
                    nv21[uvIndex++] = (byte) (U < 0 ? 0 : (U > 255 ? 255 : U));
                }

                ++index;
            }
        }
        return nv21;
    }

}
