package com.lyn.face_doorkeeper.utils;

/**
 * YUV数据转换
 */
public class YuvUtils {
    private static class YuvUtilsTypeClass {
        private static YuvUtils instance = new YuvUtils();
    }

    public static YuvUtils getInstance() {
        return YuvUtilsTypeClass.instance;
    }

    private volatile byte[] clipData;

    /**
     * Nv21裁切
     *
     * @param data       源数据
     * @param width      源宽
     * @param height     源高
     * @param left
     * @param top
     * @param clipWidth
     * @param clipHeight
     * @return
     */
    public synchronized byte[] ClipNV21(byte[] data, int width, int height, int left, int top, int clipWidth, int clipHeight) {
        if (left > width || top > height || left + clipWidth > width || top + clipHeight > height) {
            return null;
        }
        //取偶
        int x = left / 4 * 4, y = top / 4 * 4;
        int w = clipWidth / 4 * 4, h = clipHeight / 4 * 4;
        int y_unit = w * h;
        int uv = y_unit / 2;
        if (clipData == null) {
            clipData = new byte[y_unit + uv];
        } else {
            if (clipData.length != y_unit + uv) {
                clipData = new byte[y_unit + uv];
            }
        }
        int uv_index_dst = w * h - y / 2 * w;
        int uv_index_src = width * height + x;
        for (int i = y; i < y + h; i++) {
            System.arraycopy(data, i * width + x, clipData, (i - y) * w, w);//y内存块复制
            if (i % 2 == 0) {
                System.arraycopy(data, uv_index_src + (i >> 1) * width, clipData, uv_index_dst + (i >> 1) * w, w);//uv内存块复制
            }
        }
        return clipData;
    }

    /**
     * NV21镜像
     *
     * @param data   源数据
     * @param width  源宽
     * @param height 源高
     * @return
     */
    public synchronized byte[] MirrorNV21(byte[] data, int width, int height) {
        int i;
        int left, right;
        byte temp;
        int startPos = 0;

        // mirror Y
        for (i = 0; i < height; i++) {
            left = startPos;
            right = startPos + width - 1;
            while (left < right) {
                temp = data[left];
                data[left] = data[right];
                data[right] = temp;
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
                temp = data[left];
                data[left] = data[right];
                data[right] = temp;
                left++;
                right--;

                temp = data[left];
                data[left] = data[right];
                data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
        return data;
    }


    private volatile byte[] whirling90Data;

    /**
     * NV21数据旋转90度
     *
     * @param data   源数据
     * @param width
     * @param height
     * @return
     */
    public synchronized byte[] Whirling90(byte[] data, int width, int height) {
        if (whirling90Data == null) {
            whirling90Data = new byte[width * height * 3 / 2];
        } else {
            if (whirling90Data.length != width * height * 3 / 2) {
                whirling90Data = new byte[width * height * 3 / 2];
            }
        }
        // 旋转 Y 亮度
        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                whirling90Data[i] = data[y * width + x];
                i++;
            }
        }
        // 旋转 U 和 V 颜色分量
        i = width * height * 3 / 2 - 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                whirling90Data[i] = data[(width * height) + (y * width) + x];
                i--;
                whirling90Data[i] = data[(width * height) + (y * width) + (x - 1)];
                i--;
            }
        }
        return whirling90Data;
    }

    private volatile byte[] whirling180Data;

    /**
     * NV21旋转180度
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public synchronized byte[] Whirling180(byte[] data, int width, int height) {
        if (whirling180Data == null) {
            whirling180Data = new byte[width * height * 3 / 2];
        } else {
            if (whirling180Data.length != width * height * 3 / 2) {
                whirling180Data = new byte[width * height * 3 / 2];
            }
        }
        // 旋转 Y 亮度
        int i = 0;
        int count = 0;

        for (i = width * height - 1; i >= 0; i--) {
            whirling180Data[count] = data[i];
            count++;
        }
        // 旋转 U 和 V 颜色分量
        i = width * height * 3 / 2 - 1;
        for (i = width * height * 3 / 2 - 1; i >= width
                * height; i -= 2) {
            whirling180Data[count++] = data[i - 1];
            whirling180Data[count++] = data[i];
        }
        return whirling180Data;
    }

    private volatile byte[] Whirling270Data;

    /**
     * NV21旋转270度
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public synchronized byte[] Whirling270(byte[] data, int width, int height) {
        if (Whirling270Data == null) {
            Whirling270Data = new byte[width * height * 3 / 2];
        } else {
            if (Whirling270Data.length != width * height * 3 / 2) {
                Whirling270Data = new byte[width * height * 3 / 2];
            }
        }
        // 旋转 Y 亮度
        int i = 0;
        for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                Whirling270Data[i] = data[y * width + x];
                i++;
            }
        }
        // 旋转 U 和 V 颜色分量
        i = width * height;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                Whirling270Data[i] = data[(width * height) + (y * width) + (x - 1)];
                i++;
                Whirling270Data[i] = data[(width * height) + (y * width) + x];
                i++;
            }
        }
        return Whirling270Data;
    }


    private volatile byte[] NV21ConversionYUV420PData;

    /**
     * NV21转换YUV420P
     * @param data
     * @param width
     * @param height
     * @return
     */
    public synchronized byte[] NV21ConversionYUV420P(byte[] data, int width, int height) {
        if (data==null){
            return null;
        }
        int ySize = width * height;
        int uSize = width * height * 1 / 4;

        if (NV21ConversionYUV420PData == null) {
            NV21ConversionYUV420PData = new byte[width * height * 3 / 2];
        } else {
            if (NV21ConversionYUV420PData.length != width * height * 3 / 2) {
                NV21ConversionYUV420PData = new byte[width * height * 3 / 2];
            }
        }
        // y
        System.arraycopy(data, 0, NV21ConversionYUV420PData, 0, ySize);
        // u, 1/4
        int srcPointer = ySize;
        int dstPointer = ySize;
        int count = uSize;
        while (count > 0) {
            srcPointer++;
            NV21ConversionYUV420PData[dstPointer] = data[srcPointer];
            dstPointer++;
            srcPointer++;
            count--;
        }
        // v, 1/4
        srcPointer = ySize;

        count = uSize;
        while (count > 0) {
            NV21ConversionYUV420PData[dstPointer] = data[srcPointer];
            dstPointer++;
            srcPointer += 2;
            count--;
        }
        return NV21ConversionYUV420PData;
    }
}
