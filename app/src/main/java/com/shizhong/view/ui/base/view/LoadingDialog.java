package com.shizhong.view.ui.base.view;

import com.shizhong.view.ui.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class LoadingDialog extends AlertDialog {
	private CharSequence message;
	private TextView mTextView;

	public LoadingDialog(Context context) {
		  this(context, R.style.loadingDialog);
	}

	public LoadingDialog(Context context, CharSequence message) {
		this(context);
		this.message = message;
	}
	
	 public LoadingDialog(Context context, CharSequence message, int theme) {
	        this(context, theme);
	        this.message = message;
	    }

	    public LoadingDialog(Context context, int theme) {
	        super(context, theme);
	    }

	    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
	        super(context, cancelable, cancelListener);
	    }
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.loading_dialog_layout);
	        setCanceledOnTouchOutside(false);
	        initMessage();
	        initProgress();
	    }
	    
	    private void initProgress() {
			findViewById(R.id.loading);
		}

		private void initMessage() {
	        if (message != null && message.length() > 0) {
	        	mTextView= ((TextView) findViewById(R.id.dmax_spots_title));
	        	mTextView.setText(message);
	        }
	    }
		
		@Override
		public void setMessage(CharSequence message) {
			// TODO Auto-generated method stub
			super.setMessage(message);
			if(!TextUtils.isEmpty(message)&&mTextView!=null){
				mTextView.setText(message);
			}
			
		}
}
