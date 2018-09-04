package com.shizhong.view.ui.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by yuliyan on 16/8/21.
 */
public class NoFocationTextView extends TextView{
    public NoFocationTextView(Context context) {
        super(context);
    }

    public NoFocationTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoFocationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
