package com.shizhong.view.ui.base.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoViewPager extends ViewPager {

	@SuppressWarnings("unused")
	private int downX;
	@SuppressWarnings("unused")
	private int downY;

	public NoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NoViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			
			break;
		case MotionEvent.ACTION_MOVE:
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
			
		case MotionEvent.ACTION_UP:
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		default:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(arg0);
	}
}
