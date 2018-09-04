package com.shizhong.view.ui.base.view;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseWindow;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ApplyeClubAlterWindow extends BaseWindow implements View.OnClickListener {

	private ImageView mBack;

	public ApplyeClubAlterWindow(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	public ApplyeClubAlterWindow(Context context, int width, int height) {
		super(context, width, height);
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.club_apply_arter_window_layout, null);
		mRootView.setOnClickListener(this);
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setWidth(width);
		setHeight(height);
		initView(mRootView);
		setContentView(mRootView);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView(View view) {
		mBack = (ImageView) view.findViewById(R.id.left_bt);
		mBack.setOnClickListener(this);
	}

	@Override
	public void show(View view) {
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			((Activity) mContext).finish();
			break;
		}
	}

}
