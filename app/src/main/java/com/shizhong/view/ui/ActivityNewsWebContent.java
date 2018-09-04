package com.shizhong.view.ui;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.adapter.NewFriendsAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ProgressWebView;
import com.shizhong.view.ui.base.view.ShareWebThreePlateformWindow;
import com.shizhong.view.ui.bean.ShareContentBean;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

public class ActivityNewsWebContent extends BaseFragmentActivity implements OnClickListener {

	private String web_url = null;// "http://192.168.1.112:8080/portal/news/asd";
	private ProgressWebView mWebView;
	private ImageView mShareBtn;
	private String mTitle;
	private String mContentText;
	private String mImageUrl;
	private Title mTitleObj = new Title();
	private Content mContentObj = new Content();
	private Image mImageObj = new Image();
	private ViewGroup mWebContainer;

	@Override
	public void initBundle() {
		web_url = getIntent().getStringExtra(ContantsActivity.JieQu.NEWS_URL);
	}


	@Override
	public void initView() {
		setContentView(R.layout.activity_with_webview);
		((TextView) findViewById(R.id.title_tv)).setText("热门资讯");
		findViewById(R.id.left_bt).setOnClickListener(this);
		mShareBtn = (ImageView) findViewById(R.id.right_bt);
		mShareBtn.setOnClickListener(this);
		mWebContainer=(ViewGroup) findViewById(R.id.web_container);
		mWebView = new ProgressWebView(getApplicationContext(),null);
		mWebContainer.addView(mWebView);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString("mac os");
		// 支持中文，否则页面中中文显示乱码
		webSettings.setDefaultTextEncodingName("utf-8");
		if (Build.VERSION.SDK_INT >= 19) {
			webSettings.setLoadsImagesAutomatically(true);
		} else {
			webSettings.setLoadsImagesAutomatically(false);
		}

		if (!TextUtils.isEmpty(web_url)) {
			mWebView.loadUrl(web_url + "?viewType=app");
		}

		mWebView.addJavascriptInterface(mTitleObj, "title");
		mWebView.addJavascriptInterface(mImageObj, "shareImageUrl");
		mWebView.addJavascriptInterface(mContentObj, "content");
		mWebView.setWebViewClient(webViewClient);
		mWebView.setWebChromeClient(webChromeClient);
	}

	WebViewClient webViewClient=new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			if (!view.getSettings().getLoadsImagesAutomatically()) {
				view.getSettings().setLoadsImagesAutomatically(true);
			}
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//				super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}
	};

	WebChromeClient webChromeClient=new WebChromeClient() {

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// TODO Auto-generated method stub
			result.confirm();
			return super.onJsAlert(view, url, message, result);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			((TextView) findViewById(R.id.title_tv)).setText(title);

		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				mWebView.setProgressbarVisibility(false);
				mWebView.loadUrl("javascript:getTitleForAndroid()");
				mWebView.loadUrl("javascript:getShareImageUrlForAndroid()");
				mWebView.loadUrl("javascript:getContentForAndroid()");
			} else {
				mWebView.setProgressbarVisibility(true);
				mWebView.setProgressbarProgress(newProgress);
			}
		}
	};
	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.resumeTimers();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;

		case R.id.right_bt:
			if (TextUtils.isEmpty(mTitle)) {
				ToastUtils.showShort(ActivityNewsWebContent.this, "标题为空");
				return;
			}
			if (TextUtils.isEmpty(mImageUrl)) {
				ToastUtils.showShort(ActivityNewsWebContent.this, "图片URL为空");
				return;
			}
			if (TextUtils.isEmpty(mContentText)) {
				ToastUtils.showShort(ActivityNewsWebContent.this, "内容为空");
				return;
			}
			ShareContentBean bean = new ShareContentBean();
			bean.shareContent = mContentText;
			bean.shareImage = mImageUrl;
			bean.shareTitle = mTitle;
			bean.shareUrl = web_url;
			showShareWindow(mWebView, bean);

			break;
		}

	}

	private ShareWebThreePlateformWindow mShareWindow;

	private void showShareWindow(View view, ShareContentBean sharebean) {
		if (mShareWindow == null) {
			mShareWindow = new ShareWebThreePlateformWindow(ActivityNewsWebContent.this);
			LogUtils.i("sharebean", "------------sharebean[" + sharebean.toString() + "]");
			mShareWindow.setShareBean(sharebean);
		}
		if (!mShareWindow.isShowing()) {
			mShareWindow.show(view);
		} else {
			mShareWindow.dismiss();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mShareWindow != null) {
			mShareWindow.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mWebView.canGoBack()) {
				mWebView.goBack(); // goBack()表示返回WebView的上一页面
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取标题
	 * 
	 * @author yuliyan
	 *
	 */
	class Title {

		@JavascriptInterface
		public void setTitle(String msg) {
			mTitle = msg;
			// ToastUtils.showShort(ActivityNewsWebContent.this, mTitle);
		}

	}

	/**
	 * 获取imageURL
	 * 
	 * @author yuliyan
	 *
	 */
	class Image {

		@JavascriptInterface
		public void setShareImageUrl(String msg) {
			mImageUrl = msg;
			// ToastUtils.showShort(ActivityNewsWebContent.this, mImageUrl);
		}

	}

	/**
	 * 获取内容
	 * 
	 * @author yuliyan
	 *
	 */
	class Content {

		@JavascriptInterface
		public void setContent(String msg) {
			mContentText = msg;
			// ToastUtils.showShort(ActivityNewsWebContent.this, mContentText);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destroyWebView();
		if(webViewClient!=null){
			webViewClient=null;
		}
		if(webChromeClient!=null){
			webChromeClient=null;
		}
		System.gc();

	}


	public void destroyWebView() {

		mWebContainer.removeAllViews();
		if(mWebView != null) {
			mWebView.clearHistory();
			mWebView.clearCache(true);
			mWebView.removeAllViews();
			mWebView.destroy();
			mWebView = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
		}
		System.gc();

	}
}
