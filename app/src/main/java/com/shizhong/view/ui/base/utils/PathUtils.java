package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class PathUtils {
    @SuppressWarnings("unused")
    private static final String TAG = "PathUtils";

    // 缓存，可清除
    public static String getDownloadPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "download")
                .getAbsolutePath();
    }

    // 缓存，可清除
    public static String getTempLocalWebCachePath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context,
                "tempLocalWebCache").getAbsolutePath();
    }

    // 缓存，可清除
    public static String getCropImgPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "crop")
                .getAbsolutePath();
    }

    // 应用数据
    public static String getLocalWebCachePath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context,
                "localWebCache").getAbsolutePath();
    }

    // 应用数据
    public static String getTempUploadPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "tempUpload")
                .getAbsolutePath();
    }

    // 公共目录  img
    public static String getSaveImgPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "ShiZhong/img")
                .getAbsolutePath();
    }

    // 公共目录 log
    public static String getCrashLogPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, " ShiZhong/log")
                .getAbsolutePath();
    }

    //公共目录 视频
    public static String getCrashVideoPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "ShiZhong/Video").getAbsolutePath();
    }

    //公共目录  音乐
    public static String getCrashMusicPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "ShiZhong/audio").getAbsolutePath();
    }

    //公共目录 数据库
    public static String getCrashDBPath(Context context) {
        return StorageUtils.getIndividualCacheDirectory(context, "ShiZhong/db").getAbsolutePath();
    }

    public static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }

    public static void folderScan(Context context, String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            File[] array = file.listFiles();

            for (int i = 0; i < array.length; i++) {
                File f = array[i];

                if (f.isFile()) {// FILE TYPE
                    String name = f.getName();

                    if (name.contains(".jpg")) {
                        fileScan(context, f.getAbsolutePath());
                    }
                } else {// FOLDER TYPE
                    folderScan(context, f.getAbsolutePath());
                }
            }
        }
    }

    public static void fileScan(Context context, String file) {
        Uri data = Uri.parse("file://" + file);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                data));
    }

}
