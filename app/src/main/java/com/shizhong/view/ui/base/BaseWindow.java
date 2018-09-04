package com.shizhong.view.ui.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.shizhong.view.ui.R;

public abstract class BaseWindow extends PopupWindow {
    protected Context mContext;
    protected View mRootView;
    public int width;
    public Intent mIntent = new Intent();
    public  static final boolean _IS_DEBUG=SZApplication.IS_DEBUG;

    public BaseWindow(Context context, int width, int height) {
        super(context);
        this.mContext = context;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(width);
        setHeight(height);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setBackgroundDrawable(new ColorDrawable(0xe6000000));
        this.setAnimationStyle(R.style.dialog_anim_right);

    }

    public BaseWindow(Context context) {
        this(context, (int) LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    }

    protected abstract void initData(); //初始化数据

    protected abstract void initView(View view);//初始化布局

    protected abstract void show(View view);//显示当前window对象

}
