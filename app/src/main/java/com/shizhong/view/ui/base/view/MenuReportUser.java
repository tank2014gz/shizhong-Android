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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MenuReportUser extends PopupWindow implements OnClickListener {
	private TextView mOptionButton;
	private String memberId;
	private String login_token;
	private TextView mCancelBtn;

	public MenuReportUser(String memebId, Context context) {
		this(memebId, context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	private Context mContext;
	private View mRootView;

	public MenuReportUser(String memberId, Context context, int width, int height) {
		super(context);
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x4d000000));
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		this.memberId = memberId;
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
		mOptionButton.setText("举报");
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

				reportVideo(this.memberId);

			}
		case R.id.cancel_menu:
		case R.id.window:
			dismiss();
			break;
		}
	}

	private void reportVideo(String memeberId) {
		String rootUrl = "/member/report";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", this.login_token);
		params.put("memberId", memeberId);
		LogUtils.e("用户举报", "-------");
		BaseHttpNetMananger.getInstance(mContext).postJSON(mContext, rootUrl, params, new IRequestResult() {

			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject rootJSON = new JSONObject(req);
					int code = rootJSON.getInt("code");
					switch (code) {
					case 100001:
						ToastUtils.showShort(mContext, "举报成功");
						break;
					default:
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

}
