package com.shizhong.view.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.adapter.NavigationAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.viewpagerindicator.CirclePageIndicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityNavigationImages extends BaseFragmentActivity implements OnClickListener {
	private ViewPager mNavViewPager;
	private NavigationAdapter mAdapter;
	private CirclePageIndicator mIndicator;
	private ArrayList<Bitmap> images = new ArrayList<Bitmap>();

	private TextView mLogin;
	private TextView mRegBtn;

	@Override
	protected void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_navigation);
		mAdapter = new NavigationAdapter(ActivityNavigationImages.this, images);
		mNavViewPager = (ViewPager) findViewById(R.id.pager);
		mNavViewPager.setAdapter(mAdapter);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mNavViewPager);
		mIndicator.setCurrentItem(0);
		mLogin = (TextView) findViewById(R.id.login_btn);
		mLogin.setOnClickListener(this);
		mRegBtn = (TextView) findViewById(R.id.reg_btn);
		mRegBtn.setOnClickListener(this);
	}

//	private BroadcastReceiver boradcastReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getBooleanExtra(ContantsActivity.Extra.IS_FINISH_BACK_ACTIVITY, false)) {
//				context.unregisterReceiver(this);
//				finish();
//			}
//		}
//	};

	@Override
	protected void initBundle() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(ContantsActivity.Action.ACTION_APP_REG_SUCCESS);
//		registerReceiver(this.boradcastReceiver, filter);
		loadAssestSImages();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}
//
//	private void loadAssestSImages() {
//		String firstName = "navigation/nav_";
//		String lastName = ".png";
//		StringBuilder allName;
//		AssetManager am = getAssets();
//		InputStream is = null;
//		Bitmap image = null;
//		try {
//			for (int i = 0; i < 4; i++) {
//				allName = new StringBuilder();
//				allName.append(firstName).append(i).append(lastName);
//				is = am.open(allName.toString());
//				image = getBitmapByBytes(is);
//				images.add(image);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	private void loadAssestSImages(){
		String firstName="navigation/nav_";
		String lastName=".png";
		StringBuilder allName;
		 InputStream is=null;
		Bitmap image=null;
//		try{
			for (int i = 0; i < 4; i++) {
				allName = new StringBuilder();
				allName.append(firstName).append(i).append(lastName);
				is = ActivityNavigationImages.this.getClass().getClassLoader().getResourceAsStream("assets/"+allName.toString());
				image = getBitmapByBytes(is);
				images.add(image);
			}
//		}catch(IOException e){
//			LogUitls.e("shizhong error",e.toString());
//		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			Intent intent = new Intent(ActivityNavigationImages.this, LoginActivity.class);
			startActivityForResult(intent, -1);
			break;
		case R.id.reg_btn:
			intent = new Intent(ActivityNavigationImages.this, RegNav1Activity.class);
			startActivityForResult(intent, -1);
			break;

		default:
			break;
		}

	}

	public Bitmap getBitmapByBytes(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] bs = new byte[1024];

		int len = -1;
		byte bytes[] = null;
		try {
			while ((len = is.read(bs)) != -1) {
				bos.write(bs, 0, len);
			}
			bytes = bos.toByteArray();
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 对于图片的二次采样,主要得到图片的宽与高
		int width = 0;
		int height = 0;
		int sampleSize = 1; // 默认缩放为1
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 仅仅解码边缘区域
		// 如果指定了inJustDecodeBounds，decodeByteArray将返回为空
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		// 得到宽与高
		height = options.outHeight;
		width = options.outWidth;

		// 图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
		while ((height / sampleSize > UIUtils.getScreenHeightPixels(ActivityNavigationImages.this))
				|| (width / sampleSize > UIUtils.getScreenWidthPixels(ActivityNavigationImages.this))) {
			sampleSize *= 2;
		}

		// 不再只加载图片实际边缘
		options.inJustDecodeBounds = false;
		// 并且制定缩放比例
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}
}
