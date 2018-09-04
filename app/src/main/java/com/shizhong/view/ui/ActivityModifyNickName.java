package com.shizhong.view.ui;

import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.hyphenate.easeui.ContantsActivity;

import android.app.Activity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityModifyNickName extends BaseFragmentActivity implements OnClickListener {

	private ClearEditText mModifyNickName;
	private String nickName;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_modify_nickname_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("修改昵称");
		mModifyNickName = (ClearEditText) findViewById(R.id.nick_name);
		if (TextUtils.isEmpty(nickName)) {
			mModifyNickName.setHint("修改昵称");
		} else {
			mModifyNickName.setText(nickName);
		}
		Editable etext = mModifyNickName.getText();
		Selection.setSelection(etext, etext.length());
	}

	@Override
	protected void initBundle() {
		nickName = getIntent().getStringExtra(ContantsActivity.LoginModle.NICK_NAME);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:

			nickName = mModifyNickName.getText().toString();
			if (TextUtils.isEmpty(nickName)) {
				ToastUtils.showShort(ActivityModifyNickName.this, getString(R.string.nick_name_null_err));
				return;
			}
			mIntent.setClass(ActivityModifyNickName.this, ActivityUserModify.class);
			mIntent.putExtra(ContantsActivity.LoginModle.NICK_NAME, nickName);
			setResult(Activity.RESULT_OK, mIntent);
			finish();
			break;
		case R.id.titleRight:
           
			finish();
			break;

		default:
			break;
		}

	}

}
