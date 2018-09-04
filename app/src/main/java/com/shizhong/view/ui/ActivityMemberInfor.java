package com.shizhong.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.fragment.Fragment_Me;
import com.shizhong.view.ui.fragment.OtherMemberFragement;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivityMemberInfor extends BaseFragmentActivity {

	private String memberId;
	private String userId;
	private Fragment_Me mFragmnet_me;
	private OtherMemberFragement mFragement_other;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_member_info_layout);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContantsActivity.LoginModle.IS_CANBACK, true);
		if (!TextUtils.isEmpty(memberId) && memberId.equals(userId)) {
			if (mFragmnet_me == null) {
				mFragmnet_me = new Fragment_Me();
			}
			mFragmnet_me.setArguments(bundle);
			ft.replace(R.id.member_info_layout, mFragmnet_me, null);
		} else if (!TextUtils.isEmpty(memberId) && !memberId.equals(userId)) {
			if (mFragement_other == null) {
				mFragement_other = new OtherMemberFragement();

			}
			bundle.putString(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
			mFragement_other.setArguments(bundle);
			ft.replace(R.id.member_info_layout, mFragement_other, null);

		}
		ft.commit();

	}

	@Override
	protected void initBundle() {
		memberId = getIntent().getStringExtra(ContantsActivity.LoginModle.LOGIN_USER_ID);
		userId = PrefUtils.getString(ActivityMemberInfor.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mFragmnet_me != null) {
			mFragmnet_me.hide();
		}
		if (mFragement_other != null) {
			mFragement_other.hide();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (mFragmnet_me != null) {
			mFragmnet_me.onActivityResult(requestCode, resultCode, data);
		}
		if(mFragement_other!=null){
			mFragement_other.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	

}
