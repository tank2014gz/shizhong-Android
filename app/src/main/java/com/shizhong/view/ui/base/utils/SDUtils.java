package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

@SuppressWarnings({ "unused", "deprecation" })
public class SDUtils {
	/**
	 * 挂载
	 */
	public static final int ACTION_MOUNTED = 1;//
	/**
	 * 没有挂载sd卡
	 */
	public static final int ERROR_ACTION_UNMOUNTED = -1;//
	/**
	 * 已经满了
	 */
	public static final int ERROR_ACTION_MEDIA_FULL = 2;//
	/**
	 * 挂载且正常运行
	 */
	public static final int ACTION_RUN_MOUNTED = 3;//

	private static boolean isSDCardFull() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSize();
		long totalBlocks = statFs.getBlockCount();
		long availableBlocks = statFs.getAvailableBlocks();
		if (availableBlocks * blockSize == 0) {
			return true;
		}
		return false;
	}

	public final static int AVAILABLE_SPACE = 200;// M

	/**
	 * 检测用户手机是否剩余可用空间200M以上
	 * 
	 * @return
	 */
	public static boolean isAvailableSpace(Context context) {
		if (context == null) {
			return false;
		}
		// 检测磁盘空间
		if (getSDFreeSize()< AVAILABLE_SPACE) {
			ToastUtils.showShort(context, "sd卡内存不足");
			return false;
		}

		return true;
	}


	/**
	 * SD卡当前状态
	 *
	 * @return
	 */
	public static int sd_media_mounted_station() {
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			if (isSDCardFull()) {
				return ERROR_ACTION_MEDIA_FULL;
			} else {
				return ACTION_RUN_MOUNTED;
			}
		} else {
			return ERROR_ACTION_UNMOUNTED;
		}
	}

	/**
	 * 获得SD卡总容量
	 *
	 * @return
	 */
	public long getSDAllSize() {
		if (sd_media_mounted_station() == ACTION_RUN_MOUNTED) {
			// 取得SD卡文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 获取所有数据块数
			long allBlocks = sf.getBlockCount();
			// 返回SD卡大小
			// return allBlocks * blockSize; //单位Byte
			// return (allBlocks * blockSize)/1024; //单位KB
			return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
		} else {
			return 0;
		}
	}

	/**
	 * 获得SD卡的剩余容量
	 *
	 * @return
	 */
	public static long getSDFreeSize() {
		if (sd_media_mounted_station() == ACTION_RUN_MOUNTED) {
			// 取得SD卡文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = sf.getAvailableBlocks();
			// 返回SD卡空闲大小
			// return freeBlocks * blockSize; //单位Byte
			// return (freeBlocks * blockSize)/1024; //单位KB
			return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
		} else {
			return 0;
		}
	}

}
