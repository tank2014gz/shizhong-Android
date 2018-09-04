package com.shizhong.view.ui;

import android.net.http.SslError;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.view.ProgressWebView;

public class ActivitySettingAbout1 extends BaseFragmentActivity implements OnClickListener {

	private ProgressWebView mAboutWeb;
	private String web_url="http://shizhongapp.com";
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setting_abuout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("关于");
		mAboutWeb=(ProgressWebView)findViewById(R.id.about_web);
		mAboutWeb.getSettings().setDefaultTextEncodingName("utf-8");
		mAboutWeb.getSettings().setJavaScriptEnabled(true);
		mAboutWeb.setWebViewClient(webViewClient);
		mAboutWeb.setWebChromeClient(webChromeClient);
		mAboutWeb.loadUrl(web_url);
	}

	WebChromeClient webChromeClient=new WebChromeClient(){
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				mAboutWeb.setProgressbarVisibility(false);
			} else {
				mAboutWeb.setProgressbarVisibility(true);
				mAboutWeb.setProgressbarProgress(newProgress);
			}
		}
	};
	WebViewClient webViewClient=new WebViewClient() {
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}
	};
	@Override
	protected void initBundle() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;

		default:
			break;
		}
	}

}
