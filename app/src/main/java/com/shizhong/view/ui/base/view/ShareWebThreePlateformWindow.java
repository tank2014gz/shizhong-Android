package com.shizhong.view.ui.base.view;

import java.util.ArrayList;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.SharedOptionsAdapter;
import com.shizhong.view.ui.base.BaseUmengManager;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.ShareItemBean;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ShareWebThreePlateformWindow extends PopupWindow implements OnClickListener {
	private HorizontalListView mHorizontalListView;
	private ArrayList<ShareItemBean> mDatas = new ArrayList<ShareItemBean>();
	private BaseAdapter mDatasAdapter;
	private int[] iconImageId = new int[] { R.drawable.selector_denglu_sina_btn, R.drawable.selector_denglu_weixin_btn,
			R.drawable.selector_denglu_friends_quan_btn, R.drawable.selector_denglu_qq_btn,
			R.drawable.selector_denglu_qq_kongjian_btn };
	private String[] shareIconNames;
	private ShareContentBean shareBean;
	private TextView mCancle;
	private BaseUmengManager mShareManager;

	public void setShareBean(ShareContentBean sharebean) {
		this.shareBean = sharebean;
	}

	public ShareWebThreePlateformWindow(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	private View mRootView;
	private Context mContext;

	public ShareWebThreePlateformWindow(Context context, int width, int height) {
		super(context);
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x4D000000));
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.share_window_layout, null);
		mRootView.setOnClickListener(this);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		shareIconNames = context.getResources().getStringArray(R.array.share_names);
		init();
		initView(mRootView);
		setContentView(mRootView);
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		mShareManager = new BaseUmengManager();
		mShareManager.addSharePermission((Activity) context);
	}

	private void init() {
		int len = shareIconNames.length;
		ShareItemBean itemData = null;
		for (int i = 0; i < len; i++) {
			itemData = new ShareItemBean();
			itemData.itemPosition = i;
			itemData.itemImageId = (iconImageId[i]);
			itemData.itemName = (shareIconNames[i]);
			mDatas.add(itemData);
		}
	}

	protected void initView(View view) {
		mHorizontalListView = (HorizontalListView) view.findViewById(R.id.share_item_list);
		mDatasAdapter = new SharedOptionsAdapter(mContext, mDatas, this, 0xFF000000);
		mHorizontalListView.setAdapter(mDatasAdapter);
		mCancle = (TextView) view.findViewById(R.id.cancel);
		mCancle.setOnClickListener(this);
	}

	public void show(View view) {
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		showAtLocation(view, Gravity.BOTTOM, 0, 0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.window:
		case R.id.cancel:
			dismiss();
			break;
		case R.id.options_image:
			ShareItemBean tag = (ShareItemBean) v.getTag(R.string.app_name);
			switch (tag.itemPosition) {
			case 0:
				if (mShareManager != null && this.shareBean != null) {
					LogUtils.i("sharebean", "share sina");
					mShareManager.shareWeb((Activity) mContext, SHARE_MEDIA.SINA, shareBean);
				}

				break;
			case 1:
				if (mShareManager != null && this.shareBean != null) {
					LogUtils.i("sharebean", "share winxin");
					mShareManager.shareWeb((Activity) mContext, SHARE_MEDIA.WEIXIN, shareBean);
				}
				break;
			case 2:
				if (mShareManager != null && this.shareBean != null) {
					LogUtils.i("sharebean", "share winxipengyouquan");
					mShareManager.shareWeb((Activity) mContext, SHARE_MEDIA.WEIXIN_CIRCLE, shareBean);
				}
				break;
			case 3:
				if (mShareManager != null && this.shareBean != null) {
					LogUtils.i("sharebean", "share qq");
					mShareManager.shareWeb((Activity) mContext, SHARE_MEDIA.QQ, shareBean);
				}
				break;
			case 4:
				if (mShareManager != null && this.shareBean != null) {
					LogUtils.i("sharebean", "share qzone");
					mShareManager.shareWeb((Activity) mContext, SHARE_MEDIA.QZONE, shareBean);
				}
				break;

			default:
				break;
			}
			break;

		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i("sharebean", "onActivityResult()");
		mShareManager.onActivityResult(requestCode, resultCode, data);
	}

}