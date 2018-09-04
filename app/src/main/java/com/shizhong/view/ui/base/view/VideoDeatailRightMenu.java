package com.shizhong.view.ui.base.view;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class VideoDeatailRightMenu extends PopupWindow implements OnClickListener {

	private TextView mOptionButton;
	private String memberId;
	private String userId;
	private String videoId;
	private String login_token;
	private TextView mCancelBtn;

	private DelecteVideoCallBack mDelectedVideoCallBack;

	public interface DelecteVideoCallBack {
		public void detelectSuccess(String videoId);
	}

	public void setDelecteVideoCallBack(DelecteVideoCallBack callBack) {
		this.mDelectedVideoCallBack = callBack;
	}

	public VideoDeatailRightMenu(String memebId, String videoId, Context context) {
		this(memebId, videoId, context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	private Context mContext;
	private View mRootView;

	public VideoDeatailRightMenu(String memberId, String videoId, Context context, int width, int height) {
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
		this.userId = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		this.memberId = memberId;
		this.videoId = videoId;
		this.login_token = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.video_detail_right_menu_window_layout, null);
		mRootView.setOnClickListener(this);
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setContentView(mRootView);
	}

	protected void initView(View view) {
		mOptionButton = (TextView) view.findViewById(R.id.menu_option);
		mCancelBtn = (TextView) view.findViewById(R.id.cancel_menu);
		if (memberId.equals(userId)) {
			mOptionButton.setText("删除视频");
		} else {
			mOptionButton.setText("举报视频");
		}
		mOptionButton.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	public void show(View view) {
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_option:
			if (!TextUtils.isEmpty(memberId)) {
				if (memberId.equals(userId)) {
					if (!TextUtils.isEmpty(this.videoId)) {
						delecteVideo(this.videoId);
					}
				} else {
					if (!TextUtils.isEmpty(this.videoId)) {
						reportVideo(this.videoId);
					}
				}
			}
		case R.id.cancel_menu:
		case R.id.window:
			dismiss();
			break;
		}
	}

	private void reportVideo(String videId) {
		String rootUrl = "/video/report";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", this.login_token);
		params.put("videoId", this.videoId);
		LogUtils.e("视频举报", "-------");
		BaseHttpNetMananger.getInstance(mContext).postJSON(mContext, rootUrl, params, new IRequestResult() {

			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject rootJSON = new JSONObject(req);
					int code = rootJSON.getInt("code");
					switch (code) {
					case 100001:
						ToastUtils.showShort(mContext, "举报成功");
						dismiss();
						break;
					default:
						dismiss();
						ToastUtils.showShort(mContext, "举报失败");
						break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestNetExeption() {
				ToastUtils.showShort(mContext, mContext.getString(R.string.net_error));
			}

			@Override
			public void requestFail() {
				ToastUtils.showShort(mContext, mContext.getString(R.string.net_conected_error));
			}
		}, false);
	}

	private void delecteVideo(final String videoId) {
		String rootUrl = "/video/delete";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", this.login_token);
		params.put("videoId", this.videoId);
		LogUtils.e("视频删除", "-------");
		BaseHttpNetMananger.getInstance(mContext).postJSON(mContext, rootUrl, params, new IRequestResult() {

			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject rootJSON = new JSONObject(req);
					int code = rootJSON.getInt("code");
					String msg = null;
					switch (code) {
					case 100001:
						if (mDelectedVideoCallBack != null) {
							mDelectedVideoCallBack.detelectSuccess(videoId);
						}
						dismiss();
						ToastUtils.showShort(mContext, "视频删除成功");
						break;
					case 900001:
						msg = rootJSON.getString("msg");
					default:
						dismiss();
						ToastUtils.showErrorToast(mContext, code, msg, true);
						break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestNetExeption() {
				ToastUtils.showShort(mContext, mContext.getString(R.string.net_error));
			}

			@Override
			public void requestFail() {
				ToastUtils.showShort(mContext, mContext.getString(R.string.net_conected_error));
			}
		}, false);
	}

}
