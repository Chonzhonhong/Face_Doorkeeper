package com.utils;

/**
 * @author: 龙永宁
 * @date: 2020/6/27
 * Describe:
 */
public class Nv21Cut {

    /**
     * NV21裁剪 算法效率 3ms
     *
     * @param src    源数据
     * @param width  源宽
     * @param height 源高
     * @param left   顶点坐标
     * @param top    顶点坐标
     * @param clip_w 裁剪后的宽
     * @param clip_h 裁剪后的高
     * @return 裁剪后的数据
     */
    private static Object objectclipNV21=new Object();
    public static byte[] clipNV21(byte[] src, int width, int height, int left, int top, int clip_w, int clip_h) {
        synchronized (objectclipNV21){
            if (left > width || top > height || left + clip_w > width || top + clip_h > height) {
                return null;
            }
            //取偶
            int x = left / 4 * 4, y = top / 4 * 4;
            int w = clip_w / 4 * 4, h = clip_h / 4 * 4;
            int y_unit = w * h;
            int uv = y_unit / 2;
            byte[] nData = new byte[y_unit + uv];
            int uv_index_dst = w * h - y / 2 * w;
            int uv_index_src = width * height + x;
            for (int i = y; i < y + h; i++) {
                System.arraycopy(src, i * width + x, nData, (i - y) * w, w);//y内存块复制
                if (i % 2 == 0) {
                    System.arraycopy(src, uv_index_src + (i >> 1) * width, nData, uv_index_dst + (i >> 1) * w, w);//uv内存块复制
                }
            }
            return nData;
        }

    }

    /**
     * NV21裁剪 算法效率 3ms
     *
     * @param src    源数据
     * @param width  源宽
     * @param height 源高
     * @param left   顶点坐标
     * @param top    顶点坐标
     * @param clip_w 裁剪后的宽
     * @param clip_h 裁剪后的高
     * @return 裁剪后的数据
     */
    private static Object objectclipNV212=new Object();
    public static byte[] clipNV212(byte[] src, int width, int height, int left, int top, int clip_w, int clip_h) {
        synchronized (objectclipNV212){
            if (left > width || top > height || left + clip_w > width || top + clip_h > height) {
                return null;
            }
            //取偶
            int x = left / 4 * 4, y = top / 4 * 4;
            int w = clip_w / 4 * 4, h = clip_h / 4 * 4;
            int y_unit = w * h;
            int uv = y_unit / 2;
            byte[] nData = new byte[y_unit + uv];
            int uv_index_dst = w * h - y / 2 * w;
            int uv_index_src = width * height + x;
            for (int i = y; i < y + h; i++) {
                System.arraycopy(src, i * width + x, nData, (i - y) * w, w);//y内存块复制
                if (i % 2 == 0) {
                    System.arraycopy(src, uv_index_src + (i >> 1) * width, nData, uv_index_dst + (i >> 1) * w, w);//uv内存块复制
                }
            }
            return nData;
        }

    }

    //NV21: YYYY VUVU

    /**
     * 镜像
     *
     * @param nv21_data
     * @param width
     * @param height
     * @return 镜像NV21数据
     */
    private static Object objectNV21_mirror=new Object();
    public static byte[] NV21_mirror(byte[] nv21_data, int width, int height) {
        synchronized (objectNV21_mirror){
            int i;
            int left, right;
            byte temp;
            int startPos = 0;

            // mirror Y
            for (i = 0; i < height; i++) {
                left = startPos;
                right = startPos + width - 1;
                while (left < right) {
                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;
                }
                startPos += width;
            }


            // mirror U and V
            int offset = width * height;
            startPos = 0;
            for (i = 0; i < height / 2; i++) {
                left = offset + startPos;
                right = offset + startPos + width - 2;
                while (left < right) {
                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;

                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;
                }
                startPos += width;
            }
            return nv21_data;
        }

    }

    /**
     * 镜像
     *
     * @param nv21_data
     * @param width
     * @param height
     * @return 镜像NV21数据
     */
    private static Object objectNV21_mirror2=new Object();
    public static byte[] NV21_mirror2(byte[] nv21_data, int width, int height) {
        synchronized (objectNV21_mirror2){
            int i;
            int left, right;
            byte temp;
            int startPos = 0;

            // mirror Y
            for (i = 0; i < height; i++) {
                left = startPos;
                right = startPos + width - 1;
                while (left < right) {
                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;
                }
                startPos += width;
            }
            // mirror U and V
            int offset = width * height;
            startPos = 0;
            for (i = 0; i < height / 2; i++) {
                left = offset + startPos;
                right = offset + startPos + width - 2;
                while (left < right) {
                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;

                    temp = nv21_data[left];
                    nv21_data[left] = nv21_data[right];
                    nv21_data[right] = temp;
                    left++;
                    right--;
                }
                startPos += width;
            }
            return nv21_data;
        }
    }

    /**
     * 裁切加镜像
     *
     * @param src
     * @param width
     * @param height
     * @param left
     * @param top
     * @param clip_w
     * @param clip_h
     * @return NV21数据
     */

    public static byte[] CutEndMirror(byte[] src, int width, int height, int left, int top, int clip_w, int clip_h) {
        return NV21_mirror(clipNV21(src, width, height, left, top, clip_w, clip_h), clip_w, clip_h);
    }
}
