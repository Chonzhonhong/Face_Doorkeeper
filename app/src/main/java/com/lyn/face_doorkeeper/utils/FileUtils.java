package com.lyn.face_doorkeeper.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.blueberry.compress.ImageCompress;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * 人员底库照片路径
     *
     * @param context
     * @return
     */
    public static String getPersonImagePath(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/PersonImage/"));
        return context.getExternalFilesDir("").getPath().concat("/PersonImage/");
    }

    /**
     * 识别记录图片路径
     *
     * @param context
     * @return
     */
    public static String getRecordImagePath(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/RecordImage/"));
        return context.getExternalFilesDir("").getPath().concat("/RecordImage/");
    }

    /**
     * 升级apk下载路径
     *
     * @param context
     * @return
     */
    public static String getUpdateApkPath(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/UpdateApk/"));
        return context.getExternalFilesDir("").getPath().concat("/UpdateApk/");
    }

    /**
     * log日志路径
     *
     * @param context
     * @return
     */
    public static String getLogPath(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/Log/"));
        return context.getExternalFilesDir("").getPath().concat("/Log/");
    }

    public static String getAssets(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/Assets/"));
        return context.getExternalFilesDir("").getPath().concat("/Assets/");
    }

    public static String getTemporary(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/Temporary/"));
        return context.getExternalFilesDir("").getPath().concat("/Temporary/");
    }

    /**
     * 获取导入失败的照片路径
     *
     * @param context
     * @return
     */

    public static String getUnImportPerson(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/UnImportPerson/"));
        return context.getExternalFilesDir("").getPath().concat("/UnImportPerson/");
    }

    public static String getUnImportPerson1(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/UnImportPerson/分辨率不过关/"));
        return context.getExternalFilesDir("").getPath().concat("/UnImportPerson/分辨率不过关/");
    }

    public static String getUnImportPerson2(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/UnImportPerson/未检测到人脸/"));
        return context.getExternalFilesDir("").getPath().concat("/UnImportPerson/未检测到人脸/");
    }


    public static String getGestures(Context context) {
        createFileDir(context.getExternalFilesDir("").getPath().concat("/Gestures/"));
        return context.getExternalFilesDir("").getPath().concat("/Gestures/");
    }

    public static String getDataBase(Context context) {
        return context.getExternalFilesDir("databases").getPath();
    }

    public static void createFileDir(String absDirName) {
        try {
            File fc = new File(absDirName);
            if (!fc.exists()) {
                fc.mkdirs();
                LogUtils.i("create new Directory:" + absDirName);
            }
        } catch (Exception e) {
            LogUtils.i(e.getMessage());
            e.printStackTrace();
        }
    }


    public static String saveBitmap(String path, String fileName, Bitmap bm) {
        try {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            if (TextUtils.isEmpty(fileName)) {
                return null;
            }
            if (bm == null) {
                return null;
            }
            ImageCompress.nativeCompressBitmap(bm, 50, path.concat(fileName), true);
            return path.concat(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param tempBitmap
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap tempBitmap, int desiredWidth, int desiredHeight) {
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
            // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
            Bitmap bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle(); // 释放Bitmap的native像素数组
            return bitmap;
        } else {
            return tempBitmap; // 如果没有缩放，那么不回收
        }
    }


    public static void saveUnBitmap(String path, String fileName, Bitmap bm, String fials) {
        LogUtils.i("path:" + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        if (bm == null) {
            return;
        }
        Bitmap bitmap1 = bm.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap1);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setTextSize(20f);
        canvas.drawText(fials, 10, 30, paint);

        File f = new File(path, fileName);
        try {
            f.createNewFile();
            FileOutputStream bos = new FileOutputStream(f);
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            LogUtils.i("saveBitmap size:" + f.length());
            bos.flush();
            bos.getFD().sync();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>将文件转成base64 字符串</p>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            FileInputStream inputFile = null;
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 删除文件
     *
     * @param
     */
    public static void deleteFile(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
        } else {
            folder.delete();
        }
    }

    //拷贝文件到指定目录
    public static void copyFileFileStreams(File source, File dest) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(dest);
            byte[] buff = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件目录
     *
     * @param srcDir  要复制的源目录 eg:/mnt/sdcard/DB
     * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
     * @return
     */
    public static boolean copyDir(String srcDir, String destDir) {
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if (!sourceDir.exists()) {
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            //创建目标目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {//如果如果是子目录进行递归
                    copyDir(fileList[i].getPath() + "/",
                            destDir + fileList[i].getName() + "/");
                } else {//如果是文件则进行文件拷贝
                    copyFile(fileList[i].getPath(), destDir + fileList[i].getName());
                }
            }
            return true;
        } else {
            copyFileToDir(srcDir, destDir);
            return true;
        }
    }

    /**
     * 复制文件（非目录）
     *
     * @param srcFile  要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    public static boolean copyFile(String srcFile, String destFile) {
        try {
            FileInputStream streamFrom = new FileInputStream(srcFile);
            FileOutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.getFD().sync();
            streamFrom.close();
            streamTo.getFD().sync();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 把文件拷贝到某一目录下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir) {
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir + "/" + new File(srcFile).getName();
        try {
            FileInputStream streamFrom = new FileInputStream(srcFile);
            FileOutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.getFD().sync();
            streamFrom.close();
            streamTo.getFD().sync();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String ReadTxt(String strFilePath, String code) {
        try {
            File file = new File(strFilePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.trim().equals(code)) {
                    br.close();
                    return br.readLine().trim();
                }
            }
            br.close();
            Runtime.getRuntime().exec("sync");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readTxt(String txtPath) {
        try {
            File file = new File(txtPath);
            if (file.isFile() && file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuffer sb = new StringBuffer();
                    String text = null;
                    while ((text = bufferedReader.readLine()) != null) {
                        sb.append(text);
                    }
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void writeTxt(String path, String content) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> readDirectory(String path) {
        List<String> itemList = new ArrayList<>();
        try {
            File fc = new File(path);

            if (fc.isDirectory()) {
                File[] fileArray = fc.listFiles();
                for (File f : fileArray) {
                    itemList.add(f.getAbsolutePath());
                }
                return itemList;
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Url解析设备目录
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    public static String getPathFromUri(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        // 判斷是否為Android 4.4之後的版本
        final boolean after44 = Build.VERSION.SDK_INT >= 19;
        if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            final String authority = uri.getAuthority();
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents".equals(authority)) {
                // 外部儲存空間
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                if ("primary".equals(type)) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(divide[1]);
                    return path;
                } else {
                    String path = "/storage/".concat(type).concat("/").concat(divide[1]);
                    return path;
                }
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // 下載目錄
                final String docId = DocumentsContract.getDocumentId(uri);
                if (docId.startsWith("raw:")) {
                    final String path = docId.replaceFirst("raw:", "");
                    return path;
                }
                final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("connection://downloads/public_downloads"), Long.parseLong(docId));
                String path = queryAbsolutePath(context, downloadUri);
                return path;
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // 圖片、影音檔案
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                Uri mediaUri = null;
                if ("image".equals(type)) {
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    return null;
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
                String path = queryAbsolutePath(context, mediaUri);
                return path;
            }
        } else {
            // 如果是一般的URI
            final String scheme = uri.getScheme();
            String path = null;
            if ("connection".equals(scheme)) {
                // 內容URI
                path = queryAbsolutePath(context, uri);
            } else if ("file".equals(scheme)) {
                // 檔案URI
                path = uri.getPath();
            }
            return path;
        }
        return null;
    }


    /**
     * 8.1系统获取sd卡根目录
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getSdCardPath(Context context) {

        int TYPE_PUBLIC = 0;

        File file = null;

        String path = null;

        StorageManager mStorageManager = context.getSystemService(StorageManager.class);


        Class<?> mVolumeInfo = null;
        try {
            mVolumeInfo = Class.forName("android.os.storage.VolumeInfo");


            Method getVolumes = mStorageManager.getClass().getMethod(
                    "getVolumes");


            Method volType = mVolumeInfo.getMethod("getType");

            Method isMount = mVolumeInfo.getMethod("isMountedReadable");

            Method getPath = mVolumeInfo.getMethod("getPath");

            List<Object> mListVolumeinfo = (List<Object>) getVolumes
                    .invoke(mStorageManager);


            Log.d("getSdCardPath", "mListVolumeinfo.size=" + mListVolumeinfo.size());

            for (int i = 0; i < mListVolumeinfo.size(); i++) {

                int mType = (Integer) volType.invoke(mListVolumeinfo.get(i));

                Log.d("getSdCardPath", "mType=" + mType);

                if (mType == TYPE_PUBLIC) {
                    boolean misMount = (Boolean) isMount.invoke(mListVolumeinfo.get(i));
                    Log.d("getSdCardPath", "misMount=" + misMount);
                    if (misMount) {
                        file = (File) getPath.invoke(mListVolumeinfo.get(i));
                        if (file != null) {
                            path = file.getAbsolutePath();
                            Log.d("getSdCardPath", "path=" + path);
                            return path;
                        }
                    }
                }

            }

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 7.1获取外置sd卡目录
     *
     * @param mContext
     * @param is_removale
     * @return
     */
    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 检测sd卡内存大小
     *
     * @param existed
     * @param path
     */

    public void volumeSizeTest2(boolean existed, String path) {
        if (existed) {
            StatFs f = new StatFs(path);
            long blockSize = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = f.getBlockSizeLong();
                long totalSize = f.getBlockCountLong();
                long availableBlocks = f.getAvailableBlocksLong();
                double sd1 = (blockSize * totalSize * 1.0) / 1024 / 1024 / 1024;
                double sd2 = (blockSize * availableBlocks * 1.0) / 1024 / 1024 / 1024;

                String totalSize1 = String.valueOf(sd1);
                //已用
                String str1 = totalSize1.substring(0, 5);

                String availableBlocks1 = String.valueOf(sd2);
                //总共
                String str2 = availableBlocks1.substring(0, 5);
            }
        } else {

        }
    }


    public static String queryAbsolutePath(final Context context, final Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * 获取网络图片bitmap
     *
     * @param url 网络地址
     * @return bitmap
     */
    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 将文件转为byte[]
     *
     * @param filePath 文件路径
     * @return
     */
    public static byte[] getBytes(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, b.length);
            }
            out.close();
            in.getFD().sync();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;
    }

    public static void copyAssetsFileToFolder(Context context, String fileName, String folderPath) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            File file = new File(folderPath, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[inputStream.available()];
            int i = 0;
            while ((i = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, i);
            }
            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
}
