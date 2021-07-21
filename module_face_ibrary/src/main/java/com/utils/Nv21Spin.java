package com.utils;

/**
 * @author: 龙永宁
 * @date: 2020/10/26
 */
public class Nv21Spin {

    /**
     * 此处为顺时针旋转旋转90度
     *
     * @param data 旋转前的数据
     * @param imageWidth 旋转前数据的宽
     * @param imageHeight 旋转前数据的高
     * @return 旋转后的数据
     */
    private static Object objectrotateYUV420Degree90 = new Object();

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

        synchronized (objectrotateYUV420Degree90) {
            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
            // Rotate the Y luma
            int i = 0;
            for (int x = 0; x < imageWidth; x++) {
                for (int y = imageHeight - 1; y >= 0; y--) {
                    yuv[i] = data[y * imageWidth + x];
                    i++;
                }
            }
            // Rotate the U and V color components
            i = imageWidth * imageHeight * 3 / 2 - 1;
            for (int x = imageWidth - 1; x > 0; x = x - 2) {
                for (int y = 0; y < imageHeight / 2; y++) {
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                    i--;
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                    i--;
                }
            }
            return yuv;
        }

    }

    //顺时针旋转180度
    private static Object ObjectrotateYUV420Degree180 = new Object();

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        synchronized (ObjectrotateYUV420Degree180) {
            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

            int i = 0;
            int count = 0;

            for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
                yuv[count] = data[i];
                count++;
            }

            i = imageWidth * imageHeight * 3 / 2 - 1;
            for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                    * imageHeight; i -= 2) {
                yuv[count++] = data[i - 1];
                yuv[count++] = data[i];
            }
            return yuv;
        }

    }


    /**
     * 此处为顺时针旋转270
     *
     * @param data 旋转前的数据
     * @param imageWidth 旋转前数据的宽
     * @param imageHeight 旋转前数据的高
     * @return 旋转后的数据
     */
    private static Object objectrotateYUV420Degree270 = new Object();

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        synchronized (objectrotateYUV420Degree270) {
            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

            // Rotate the Y luma

            int i = 0;


            for (int x = imageWidth - 1; x >= 0; x--) {

                for (int y = 0; y < imageHeight; y++) {

                    yuv[i] = data[y * imageWidth + x];
                    i++;

                }
            }// Rotate the U and V color components
            i = imageWidth * imageHeight;

            for (int x = imageWidth - 1; x > 0; x = x - 2) {

                for (int y = 0; y < imageHeight / 2; y++) {
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                    i++;
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                    i++;

                }

            }

            return yuv;
        }


    }
}