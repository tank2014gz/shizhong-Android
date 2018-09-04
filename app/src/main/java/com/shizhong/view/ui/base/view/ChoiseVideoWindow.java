package com.shizhong.view.ui.base.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.VideoLocalAdapter;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.StringUtils;
import com.shizhong.view.ui.base.utils.VideoUtils;
import com.shizhong.view.ui.bean.VideoLocalBean;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ChoiseVideoWindow extends PopupWindow implements OnClickListener {

	private View mNullContentView;
	private TextView mNullDesView;
	private Context mContext;
	private View mRootView;
	private List<VideoLocalBean> mDatas = new ArrayList<VideoLocalBean>();

	private TextView mCancleBtn;
	private GridView mVideosGridView;
	private VideoLocalAdapter mAdapter = null;
	private boolean isRelease = false;

	public ChoiseVideoWindow(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	public ChoiseVideoWindow(Context context, int width, int height) {
		super(context);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x4d000000));
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		mContext = context;
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.activity_video_selecte_layout, null);
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setContentView(mRootView);
		initData();
	}

	private void initView(View rootView) {
		mNullContentView = rootView.findViewById(R.id.null_view);
		mNullDesView = (TextView) rootView.findViewById(R.id.tv_null_text);
		mNullDesView.setText("暂时没发现视频资源");
		rootView.findViewById(R.id.left_bt).setVisibility(View.GONE);
		((TextView) rootView.findViewById(R.id.title_tv)).setText("选择视频");
		mCancleBtn = ((TextView) rootView.findViewById(R.id.titleRight));
		mCancleBtn.setVisibility(View.VISIBLE);
		mCancleBtn.setCompoundDrawables(null, null, null, null);
		mCancleBtn.setText("取消");
		mCancleBtn.setOnClickListener(this);
		mVideosGridView = (GridView) rootView.findViewById(R.id.video_grid);
		mAdapter = new VideoLocalAdapter(mContext, mDatas);
		mVideosGridView.setAdapter(mAdapter);
		mAdapter.setWindow(this);
	}

	private void initData() {
		new AsyncTask<Void, Void, Boolean>() {
			protected void onPreExecute() {
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				mDatas.clear();
				Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = mContext.getContentResolver();
				Cursor cursor = cr.query(originalUri, null, null, null, MediaColumns.DATE_MODIFIED + " DESC");
				if (cursor == null) {
					return false;
				}
				String path = null;
				long imageId = 0;
				long modified = 0;
				String duringDes = "";
				long during = 0;
				while (cursor.moveToNext() && !isRelease) {
					imageId = cursor.getLong(cursor.getColumnIndex("_ID"));
					path = cursor.getString(cursor.getColumnIndex("_data"));
					modified = cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_MODIFIED));
					during = VideoUtils.getVideoDuring(path);
					duringDes = StringUtils.formatVideoTime((int) during);
					if (during >= 5000 && during <= 600000) {
						if (!path.contains("ShiZhong")) {
							if (FileUtils.checkFile(path)) {
								VideoLocalBean bean = new VideoLocalBean();
								bean._id = imageId;
								bean.path = path;
								bean.modified = modified;
								bean.durationDes = duringDes;
								bean.during = during;
								if (mDatas != null) {
									if (!mDatas.contains(bean)) {
										mDatas.add(bean);
									}
								}
							}
						}
					}
				}
				cursor.close();
				if (mDatas != null) {
					Collections.sort(mDatas, new Comparator<VideoLocalBean>() {
						@Override
						public int compare(VideoLocalBean lhs, VideoLocalBean rhs) {
							if (lhs.modified == rhs.modified)
								return 0;
							else
								return rhs.modified > lhs.modified ? 1 : -1;
						}

					});
				}
				return mDatas == null || mDatas.size() <= 0;
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					mNullContentView.setVisibility(View.VISIBLE);
				} else {
					mAdapter.notifyDataSetChanged();
					mNullContentView.setVisibility(View.GONE);
				}
			};
		}.execute();
	}

	public void setTopic(String topic_name, String topic_id,int topic_page) {
		mAdapter.setTopicInfo(topic_name,topic_id, topic_page);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titleRight:
			dismiss();
			break;

		default:
			break;
		}

	}

	public void show(View view) {
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		showAtLocation(view, Gravity.TOP, 0, 0);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mAdapter != null) {
			mAdapter.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void release() {
		isRelease = true;
		if (mDatas != null) {
			mDatas.clear();
			mDatas = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (mVideosGridView != null) {
			mVideosGridView = null;
		}
		System.gc();

	}

}
