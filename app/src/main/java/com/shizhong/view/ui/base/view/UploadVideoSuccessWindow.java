package com.shizhong.view.ui.base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseUmengManager;
import com.hyphenate.easeui.utils.GlideRoundTransform;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

/**
 * Created by yuliyan on 16/1/25.
 */
public class UploadVideoSuccessWindow extends PopupWindow implements View.OnClickListener, SnsPostListener {

	private String shareCoverUrl;
	private ImageView mVideoConverImage;
	private View mRootView;
	private Context mContext;

	private ShareContentBean mShareBean;
	private BaseUmengManager mShareManager;

	public UploadVideoSuccessWindow(Context context, int width, int height) {
		super(context);
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x4D000000));
		mRootView = LayoutInflater.from(context).inflate(R.layout.video_upload_success_layout, null);
		mRootView.setOnClickListener(this);
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setWidth(width);
		setHeight(height);
		setContentView(mRootView);
		mShareManager = new BaseUmengManager();
		mShareManager.addSharePermission((Activity) context);
	}

	public UploadVideoSuccessWindow(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

	}

	protected void initView(View view) {
		mVideoConverImage = (ImageView) view.findViewById(R.id.video_cover_image);
		view.findViewById(R.id.parent_view).setOnClickListener(this);
		view.findViewById(R.id.cancel_share).setOnClickListener(this);
		view.findViewById(R.id.share_qq).setOnClickListener(this);
		view.findViewById(R.id.share_sina).setOnClickListener(this);
		view.findViewById(R.id.share_weixin).setOnClickListener(this);
		view.findViewById(R.id.share_friends).setOnClickListener(this);

	}

	public void show(View view) {
		setAnimationStyle(R.style.dialog_anim_bottom);
		showAtLocation(view, Gravity.CENTER, 0, 0);

	}

	public void setShareBean(ShareContentBean sharebean) {
		this.mShareBean = sharebean;
		shareCoverUrl = FormatImageURLUtils.formatURL(this.mShareBean.shareImage, ContantsActivity.Image.SMALL,
				ContantsActivity.Image.SMALL);
		if (mVideoConverImage != null) {
			Glide.with(mContext).load(shareCoverUrl).placeholder(R.drawable.sz_activity_default)
					.error(R.drawable.sz_activity_default).transform(new GlideRoundTransform(mContext, 5)).centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(mVideoConverImage);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.parent_view:
		case R.id.cancel_share:
			dismiss();
			break;
		case R.id.share_qq:
			if (mShareManager != null && this.mShareBean != null) {
				LogUtils.i("sharebean", "share qq");
				mShareManager.sharePlactformInfo((Activity) mContext, SHARE_MEDIA.QQ, mShareBean, this);
			}
			break;
		case R.id.share_sina:
			if (mShareManager != null && this.mShareBean != null) {
				mShareManager.sharePlactformInfo((Activity) mContext, SHARE_MEDIA.SINA, mShareBean, this);
			}

			break;
		case R.id.share_friends:
			if (mShareManager != null && this.mShareBean != null) {
				LogUtils.i("sharebean", "share winxipengyouquan");
				mShareManager.sharePlactformInfo((Activity) mContext, SHARE_MEDIA.WEIXIN_CIRCLE, mShareBean, this);
			}
			break;
		case R.id.share_weixin:
			if (mShareManager != null && this.mShareBean != null) {
				LogUtils.i("sharebean", "share winxin");
				mShareManager.sharePlactformInfo((Activity) mContext, SHARE_MEDIA.WEIXIN, mShareBean, this);
			}

			break;

		}
	}

	@Override
	public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {

	}

	@Override
	public void onStart() {

	}

}
