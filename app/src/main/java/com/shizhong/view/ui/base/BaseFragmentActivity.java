package com.shizhong.view.ui.base;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.Flash_Activity;
import com.shizhong.view.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

import android.content.Intent;
import android.os.Bundle;

public class BaseFragmentActivity extends BaseUmengActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		MobclickAgent.setScenarioType(BaseFragmentActivity.this, EScenarioType.E_UM_NORMAL);
		super.onCreate(arg0);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
				switch (resultCode){
			        case ContantsActivity.Action.RESULT_LOGOUT:
						if(!(this instanceof MainActivity)){
						setResult(resultCode);
						onBackPressed();
						}else{
							onBackPressed();
							Intent intent = new Intent(this, Flash_Activity.class);
							startActivity(intent);
						}
						break;
		      }


	}
}
