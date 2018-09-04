package com.shizhong.view.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.DanceClass;
import com.shizhong.view.ui.fragment.Fragment_Dances_Page;

import java.util.List;

public class DancerViewPagerAdpater extends FragmentStatePagerAdapter {

	private Fragment_Dances_Page[] mDancerFragments;
	private List<DanceClass> titles;

		public DancerViewPagerAdpater(FragmentManager fm, Fragment_Dances_Page[] pages, List<DanceClass> titles) {
			super(fm);
			// TODO Auto-generated constructor stub
			this.mDancerFragments = pages;
			this.titles = titles;
	}

	@Override
	public Fragment getItem(int position) {
		LogUtils.e("shizhong","title:"+titles.get(position).categoryName);
		Fragment_Dances_Page item=this.mDancerFragments[position];
		if (item == null) {
			item = Fragment_Dances_Page.newInstance(titles.get(position),position);
			mDancerFragments[position] = item;
		}

		return item;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titles.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {


		LogUtils.e("shizhong","titles.get(position).categoryName----"+titles.get(position).categoryName);

		return titles.get(position).categoryName;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment_Dances_Page f = (Fragment_Dances_Page) super.instantiateItem(container, position);
		         return f;
		 }
			  @Override
	  public int getItemPosition(Object object) {
		      return PagerAdapter.POSITION_NONE;
		  }

}
