package com.shizhong.view.ui.base;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	public View mRootView;
	public Intent mIntent = new Intent();
//	public boolean isUpdateUI;// 是否更新ui
	public  static final boolean _IS_DEBUG=SZApplication.IS_DEBUG;
	public abstract void initBundle();

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(initRootView(), container, false);

		initBundle();
		initView();
		initData();
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
//		if (isUpdateUI) {

//		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
	}

	/**
	 * 查找跟布局
	 *
	 * @return
	 */
	public abstract int initRootView();

	/**
	 * 初始化布局
	 *
	 * @param
	 */
	public abstract void initView();

	/**
	 * 初始化数据
	 */
	public abstract void initData();

	/**
	 * 根据ID查找相应的view对象
	 *
	 * @param id
	 * @return
	 */
	public View findViewById(int id) {
		return mRootView.findViewById(id);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



}
