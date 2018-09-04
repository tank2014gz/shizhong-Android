package com.shizhong.view.ui.base.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 系统键盘显示、隐藏工具类
 *
 * @author Administrator
 */
public class InputMethodUtils {
    /**
     * 显示系统软键盘
     *
     * @param context
     * @param view
     */
    public static void show(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view,
                0);
    }

    /**
     * 隐藏系统软键盘
     *
     * @param context
     * @param view
     */
    public static void hide(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    public static boolean hidSoftInput(MotionEvent ev, Activity context){
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = context.getCurrentFocus();
            if (isHideInput(view, ev)) {
                InputMethodUtils.HideSoftInput(view.getWindowToken(),context);
                return true;
            }
        }
        return false;
    }
    // 隐藏软键盘
    private  static void HideSoftInput(IBinder token, Context context) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    // 判定是否需要隐藏
    private static boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }



}
