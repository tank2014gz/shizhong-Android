package com.shizhong.view.ui;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.ApplicationUtils;
import com.shizhong.view.ui.base.view.ProgressWebView;

import android.net.http.SslError;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ActivitySettingAbout extends BaseFragmentActivity implements OnClickListener {
	private  TextView app_url;
	private TextView pro_url;
	private  TextView app_version;

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setting_abuout_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("关于失重");

		app_url=(TextView) findViewById(R.id.url_app);
		app_url.setOnClickListener(this);
		pro_url=(TextView)findViewById(R.id.pro_text);
		pro_url.setOnClickListener(this);
         app_version=(TextView) findViewById(R.id.version_number);
		app_version.setText("V "+ApplicationUtils.GetVersion(getApplication()));

	}

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
			case R.id.url_app:
				mIntent.setClass(ActivitySettingAbout.this,AgreementActivity.class);
				mIntent.putExtra(ContantsActivity.WEB.TITLE,"关于失重");
				mIntent.putExtra(ContantsActivity.WEB.URL,"http://shizhongapp.com");
				startActivity(mIntent);

				break;
			case  R.id.pro_text:
				mIntent.setClass(ActivitySettingAbout.this,AgreementActivity.class);
				mIntent.putExtra(ContantsActivity.WEB.URL,"http://7xr57b.dl1.z0.glb.clouddn.com/yonghuxieyi.htm");
				mIntent.putExtra(ContantsActivity.WEB.TITLE,"《失重用户协议》");
				startActivity(mIntent);
				break;

		default:
			break;
		}
	}

}
