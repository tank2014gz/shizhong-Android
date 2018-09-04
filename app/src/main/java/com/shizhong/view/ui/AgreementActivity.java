package com.shizhong.view.ui;

import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.view.ProgressWebView;

/**
 * 用户协议 Created by yuliyan on 15/12/27.
 */
public class AgreementActivity extends BaseFragmentActivity implements OnClickListener {

	private String web_url = "http://7xr57b.dl1.z0.glb.clouddn.com/yonghuxieyi.htm";
	private ProgressWebView mWebView;
	private String title;


	@Override
	protected void initView() {
		setContentView(R.layout.layout_agreement_activity);
		if(TextUtils.isEmpty(title)){
			title="《失重用户协议》";
		}
		((TextView) findViewById(R.id.title_tv)).setText(title);
		findViewById(R.id.left_bt).setOnClickListener(this);
		mWebView = (ProgressWebView) findViewById(R.id.web_view);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 支持中文，否则页面中中文显示乱码
		webSettings.setDefaultTextEncodingName("GBK");
		mWebView.setWebViewClient(webViewClient);
		mWebView.setWebChromeClient(webChromeClient);

		if (!TextUtils.isEmpty(web_url)) {
			mWebView.loadUrl(web_url);
		}

	}

	WebChromeClient webChromeClient=new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				mWebView.setProgressbarVisibility(false);
			} else {
				mWebView.setProgressbarVisibility(true);
				mWebView.setProgressbarProgress(newProgress);
			}


		}
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// TODO Auto-generated method stub

			return super.onJsAlert(view, url, message, result);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
//			((TextView) findViewById(R.id.title_tv)).setText(title);
		}

	};

	WebViewClient webViewClient=new WebViewClient(){
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

	};

	@Override
	protected void initBundle() {
		title=getIntent().getStringExtra(ContantsActivity.WEB.TITLE);
		web_url=getIntent().getStringExtra(ContantsActivity.WEB.URL);


	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mWebView != null) {
			mWebView = null;
		}
	}
}
