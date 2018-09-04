package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Provides application storage paths
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class StorageUtils {

//	@SuppressWarnings("unused")
//	private static final String INDIVIDUAL_DIR_NAME = "uil-images";

    private StorageUtils() {
    }

    /**
     * Returns application cache directory. Cache directory will be created on
     * SD card <i>("/Android/data/[app_package_name]/cache")</i> if card is
     * mounted. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getFilesDirectory(Context context) {
        File appFileDir = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            appFileDir = getExternalFilesDir(context);
        }
        if (appFileDir == null) {
            appFileDir = context.getFilesDir();
        }
        return appFileDir;

    }

    /**
     * Returns individual application cache directory (for only image caching
     * from ImageLoader). Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache/xxx")</i> if card is mounted.
     * Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @param dirName Individual directory name
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context,
                                                   String dirName) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, dirName);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;

    }

    public static File getIndividualFilesDirectory(Context context,
                                                   String dirName) {
        File fileDir = getFilesDirectory(context);
        File individualFileDir = new File(fileDir, dirName);
        if (!individualFileDir.exists()) {
            if (!individualFileDir.mkdir()) {
                individualFileDir = fileDir;
            }
        }
        return individualFileDir;

    }

    /**
     * Returns specified application cache directory. Cache directory will be
     * created on SD card by defined path if card is mounted. Else - Android
     * defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir",
     *                 "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    cacheDir);
        }
        if (appCacheDir == null
                || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(
                Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(
                new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            try {
                new File(dataDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.w("StorageUtils",
                        "Can't create \".nomedia\" file in application external cache directory",
                        e);
            }
            if (!appCacheDir.mkdirs()) {
                Log.w("StorageUtils",
                        "Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

    private static File getExternalFilesDir(Context context) {
        File dataDir = new File(new File(
                Environment.getExternalStorageDirectory(), "Android"), "data");
        File appFileDir = new File(new File(dataDir, context.getPackageName()),
                "files");
        if (!appFileDir.exists()) {
            try {
                new File(dataDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.w("StorageUtils",
                        "Can't create \".nomedia\" file in application external files directory",
                        e);
            }
            if (!appFileDir.mkdirs()) {
                Log.w("StorageUtils",
                        "Unable to create external files directory");
                return null;
            }
        }
        return appFileDir;
    }
    
    public static File getSDRootFile(Context context){
    	
    	return Environment.getExternalStorageDirectory();
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
