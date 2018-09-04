package com.shizhong.view.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class FragmentAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragmentList;

	
	public FragmentAdapter(FragmentManager fm,List<Fragment> list) {
		super(fm);
		this.mFragmentList=list;
	}


	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.mFragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mFragmentList.size();
	}

}
