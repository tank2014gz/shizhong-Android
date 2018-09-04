package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sharedprefrences 缓存数据
 *
 * @author yuliyan
 */
public class PrefUtils {

	private static SharedPreferences mPrefs;

	public static void putBoolean(Context ctx, String key, boolean value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}

		mPrefs.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mPrefs.getBoolean(key, defValue);
	}

	public static void putString(Context ctx, String key, String value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		mPrefs.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mPrefs.getString(key, defValue);
	}

	public static void putInt(Context ctx, String key, int value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		mPrefs.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ctx, String key, int defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mPrefs.getInt(key, defValue);
	}

	public static void putLong(Context ctx, String key, long value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		mPrefs.edit().putLong(key, value);
	}

	public static long getLong(Context ctx, String key, long value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mPrefs.getLong(key, value);
	}

	public static void remove(Context ctx, String key) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}

		mPrefs.edit().remove(key).commit();
	}

	public static void clear(Context ctx) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}

		mPrefs.edit().clear().commit();
	}
	
	
	public static boolean hasString(Context ctx, String key){
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mPrefs.contains(key);
	}
}
