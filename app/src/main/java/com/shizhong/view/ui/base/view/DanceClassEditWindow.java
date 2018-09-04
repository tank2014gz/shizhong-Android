package com.shizhong.view.ui.base.view;

import android.content.Context;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.DancesClassAdapter;
import com.shizhong.view.ui.adapter.DancesDragClassAdapter;
import com.shizhong.view.ui.adapter.NewFriendsAdapter;
import com.shizhong.view.ui.base.BaseWindow;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.DanceClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuliyan on 16/1/25.
 */
public class DanceClassEditWindow extends BaseWindow implements View.OnClickListener {
	private DragGridView mDanceGridView;
	private DancesDragClassAdapter mAdapter;
	private List<DanceClass> mDatas = new ArrayList<DanceClass>();
	private ImageView mRightImage;
	private TextView mTitleView;
	private String mTitle;

	private OnDanceChoiseListener mChoiseListener;
	private  EidtListener mEditListener;

	public  void setEditListener(EidtListener listener){
		this.mEditListener=listener;
	}

	public interface OnDanceChoiseListener {
		public void choiseListener(int position);
	}

	public void setOnDanceChoiseListener(OnDanceChoiseListener listener) {
		this.mChoiseListener = listener;
	}

	public DanceClassEditWindow(Context context, int width, int height, List<DanceClass> list, String title) {

		super(context);
		this.mDatas = (ArrayList<DanceClass>) list;
		mRootView = LayoutInflater.from(context).inflate(R.layout.dance_edit_winow_layout, null);
		this.mTitle = title;
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setWidth((int) (UIUtils.getScreenWidthPixels(mContext)));
		setContentView(mRootView);

	}

	public DanceClassEditWindow(Context context, List<DanceClass> list, String title) {
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
		mDanceGridView = (DragGridView) view.findViewById(R.id.dances_grid);
		mAdapter = new DancesDragClassAdapter(mContext, mDatas);
		mDanceGridView.setAdapter(this.mAdapter);
		mDanceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(mDatas!=null&&mDatas.size()>i){
					mAdapter.notifyDataSetChanged();
					mChoiseListener.choiseListener(i);
				}
			}
		});
		mDanceGridView.setDrageFinishListener(new DragGridView.DragFinishListener() {
			@Override
			public void dragFinish() {
				mDatas=mAdapter.getDatas();
				if(mEditListener!=null){
					mEditListener.edit(mDatas);
				}
			}
		});


	}

	@Override
	public void show(View view) {
		setAnimationStyle(R.style.dialog_anim_top);
		showAtLocation(view, Gravity.TOP, 0, 0);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

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


	public  interface  EidtListener{
		public  void edit(List<DanceClass> datas);
	}
}
