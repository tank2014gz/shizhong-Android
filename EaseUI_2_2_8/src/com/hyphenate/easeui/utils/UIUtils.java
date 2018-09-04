
package com.hyphenate.easeui.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UIUtils {

    /**
     * 获得手机屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得手机屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * dip转px
     *
     * @param context
     * @param dip
     * @return
     */
    static public int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    static public float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }


    /**
     * 判断设备是否是低配机
     *
     * @param context
     * @return
     */

    public static boolean isLowDevice(Context context) {
        // Log.i("PocoUtils","NativeHeapSizeTotal:"+(Debug.getNativeHeapSize()>>10));
        // Log.i("PocoUtils","NativeAllocatedHeapSize:"+(Debug.getNativeHeapAllocatedSize()>>10));
        // Log.i("PocoUtils","NativeAllocatedFree:"+(Debug.getNativeHeapFreeSize()>>10));

        // 屏幕宽度小于480
        int screenWidth = Math.min(getScreenWidthPixels(context), getScreenWidthPixels(context));
        if (screenWidth < 480)
            return true;

        // RAM小于512M
        int totalRamSize = (int) (getTotalRAMSize() / 1024 / 1024);
        Log.i("PocoUtils", "totalRAMSize:" + totalRamSize);
        if (totalRamSize <= 400)
            return true;

        // int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024/1024);
        // Log.i("PocoUtils", "maxMemory:" + maxMemory);
        // if (maxMemory<=32) return true;// (虚拟机)最大内存少于等于32M的手机

        return false;
    }

    // RAM 总大小
    public static long getTotalRAMSize() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long totalSize = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            totalSize = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return totalSize;
    }
}
