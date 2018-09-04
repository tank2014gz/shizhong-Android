package com.shizhong.view.ui.base.view;

import com.shizhong.view.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorDialog extends AlertDialog {
	private CharSequence message;
	private TextView mReasionText;
	private Button mBackText;
	private Context mContext;

	public ErrorDialog(Context context) {
		this(context, R.style.loadingDialog);
		mContext = context;
	}

	public ErrorDialog(Context context, CharSequence message) {
		this(context);
		this.message = message;
	}

	public ErrorDialog(Context context, CharSequence message, int theme) {
		this(context, theme);
		this.message = message;
		mContext = context;
	}

	public ErrorDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public ErrorDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error_dialog_window);
		setCanceledOnTouchOutside(false);
		initMessage();
	}

	private void initMessage() {

		mBackText = (Button) findViewById(R.id.btnCancle);
		mBackText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				((Activity) mContext).finish();
			}

		});
		if (message != null && message.length() > 0) {

			mReasionText.setText(message);
		}
	}

	public void setMessage(String msg) {
		if (mReasionText == null) {
			mReasionText = ((TextView) findViewById(R.id.dialogContent));
		}
		if (mReasionText != null) {
			mReasionText.setText(msg);
		}
	}
}