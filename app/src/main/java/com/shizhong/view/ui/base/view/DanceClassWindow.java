package com.shizhong.view.ui.base.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.DancesClassAdapter;
import com.shizhong.view.ui.base.BaseWindow;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.DanceClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuliyan on 16/1/25.
 */
public class DanceClassWindow extends BaseWindow implements View.OnClickListener {
	private GridView mDanceGridView;
	private DancesClassAdapter mAdapter;
	private ArrayList<DanceClass> mDatas = new ArrayList<DanceClass>();
	private ImageView mRightImage;
	private TextView mTitleView;
	private String mTitle;

	private OnDanceChoiseListener mChoiseListener;

	public interface OnDanceChoiseListener {
		public void choiseListener(int position);
	}

	public void setOnDanceChoiseListener(OnDanceChoiseListener listener) {
		this.mChoiseListener = listener;
	}

	public DanceClassWindow(Context context, int width, int height, List<DanceClass> list, String title) {

		super(context);
		this.mDatas = (ArrayList<DanceClass>) list;
		mRootView = LayoutInflater.from(context).inflate(R.layout.dance_winow_layout, null);
		this.mTitle = title;
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setWidth((int) (UIUtils.getScreenWidthPixels(mContext)));
		setContentView(mRootView);

	}

	public DanceClassWindow(Context context, List<DanceClass> list, String title) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, list, title);

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initView(View view) {
		view.findViewById(R.id.left_bt).setVisibility(View.GONE);
		mRightImage = (ImageView) view.findViewById(R.id.right_bt);
		mRightImage.setOnClickListener(this);
		mTitleView = (TextView) view.findViewById(R.id.title_tv);
		mTitleView.setText(this.mTitle);
		mRightImage.setImageResource(R.drawable.btn_camera_close);
		mDanceGridView = (GridView) view.findViewById(R.id.dances_grid);
		mAdapter = new DancesClassAdapter(mContext, mDatas, this);
		mDanceGridView.setAdapter(this.mAdapter);

	}

	@Override
	public void show(View view) {
		setAnimationStyle(R.style.dialog_anim_top);
		showAtLocation(view, Gravity.TOP, 0, 0);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.selected:
		case R.id.dance_image_id:
			DanceClass d = (DanceClass) view.getTag(R.string.app_name);
			int pos = d.position;
			// d.isSelected=(!d.isSelected);
			mDatas.set(pos, d);
			mAdapter.notifyDataSetChanged();
			mChoiseListener.choiseListener(pos);
		case R.id.right_bt:
			dismiss();
			break;

		}
	}

	public void release() {
		if (mDatas != null) {
			mDatas.clear();
			mDatas = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (mDanceGridView != null) {
			// mDanceGridView.removeAllViews();
			mDanceGridView = null;
		}
		System.gc();

	}
}
