package com.shizhong.view.ui.adapter;

import java.util.List;

import com.shizhong.view.ui.LoginActivity;
import com.shizhong.view.ui.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NavigationAdapter extends PagerAdapter implements OnClickListener {
	private List<Bitmap> list;
	private ImageView mImageView;
	private ImageView mNextImage;
	private LayoutInflater mInflater;
	private int mChildCount = 0;
	private Context mContext;

	public NavigationAdapter(Context context, List<Bitmap> list) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.list = list;

	}

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		// TODO Auto-generated method stub
		View imageLayout = this.mInflater.inflate(R.layout.navigation_item_layout, container, false);
		mImageView = (ImageView) imageLayout.findViewById(R.id.nav_image);
		mImageView.setImageBitmap(list.get(position));
		mNextImage = (ImageView) imageLayout.findViewById(R.id.next_image);
		container.addView(imageLayout, 0);
		if (position != 3) {
			mNextImage.setVisibility(View.GONE);
		} else {
			mNextImage.setVisibility(View.VISIBLE);
			mNextImage.setOnClickListener(this);
			mNextImage.setTag(position);
		}

		return imageLayout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_image:
			int position = (Integer) v.getTag();
			if (position == (list.size() - 1)) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				((Activity) mContext).startActivityForResult(intent, -1);
				((Activity) mContext).finish();
			}

			break;

		default:
			break;
		}

	}

}
