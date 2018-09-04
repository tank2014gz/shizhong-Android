package com.shizhong.view.ui;

import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.hyphenate.easeui.ContantsActivity;

import android.app.Activity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityModifySignture extends BaseFragmentActivity implements OnClickListener {
	private EditText mModifySignture;
	private String signture;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_modify_signture_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("修改签名");
		mModifySignture = (EditText) findViewById(R.id.signture);
		if (TextUtils.isEmpty(signture)) {
			mModifySignture.setHint(getString(R.string.input_signture));
		} else {
			mModifySignture.setText(signture);
		}
		Editable etext = mModifySignture.getText();
		Selection.setSelection(etext, etext.length());
	}

	@Override
	protected void initBundle() {
		signture = getIntent().getStringExtra(ContantsActivity.LoginModle.SIGNTURE);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			signture = mModifySignture.getText().toString();
			if (TextUtils.isEmpty(signture)) {
				signture = "";
			}
			mIntent.setClass(ActivityModifySignture.this, ActivityUserModify.class);
			mIntent.putExtra(ContantsActivity.LoginModle.SIGNTURE, signture);
			setResult(Activity.RESULT_OK, mIntent);
			finish();
			break;
		}

	}

}
