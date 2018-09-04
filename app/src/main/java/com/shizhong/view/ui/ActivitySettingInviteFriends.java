package com.shizhong.view.ui;

import java.util.ArrayList;
import java.util.List;

import com.shizhong.view.ui.adapter.SharedOptionsAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.BaseUmengManager;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.ShareItemBean;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ActivitySettingInviteFriends extends BaseFragmentActivity implements OnClickListener {

	private GridView mSharedGrid;

	private String[] shareIconNames = null;
	private int[] iconImageId = new int[] { R.drawable.selector_denglu_sina_btn, R.drawable.selector_denglu_weixin_btn,
			R.drawable.selector_denglu_friends_quan_btn, R.drawable.selector_denglu_qq_btn,
			R.drawable.selector_denglu_qq_kongjian_btn };

	private List<ShareItemBean> mDatas = new ArrayList<ShareItemBean>();
	private BaseAdapter mAdapter;
	private BaseUmengManager mShareManager;
	private ShareContentBean inviteFriendsInfoBean;
	private String content_info = "我刚下载了“失重app” 这是款街舞软件，希望喜欢街舞的朋友一起来玩哦。（不论何时何地，保留住对街舞那份热爱。）";

	private void initItemData() {
		ShareItemBean item = null;
		int len = shareIconNames.length;
		for (int i = 0; i < len; i++) {
			item = new ShareItemBean();
			item.itemPosition = i;
			item.itemImageId = (iconImageId[i]);
			item.itemName = (shareIconNames[i]);
			mDatas.add(item);
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_invite_friends);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("邀请好友");
		mSharedGrid = (GridView) findViewById(R.id.share_grid);
		shareIconNames = getResources().getStringArray(R.array.share_names);
		initItemData();
		mSharedGrid = (GridView) findViewById(R.id.share_grid);
		mAdapter = new SharedOptionsAdapter(ActivitySettingInviteFriends.this, mDatas, this,0xFFFFFFFF);
		mSharedGrid.setAdapter(mAdapter);
	}

	@Override
	protected void initBundle() {
		inviteFriendsInfoBean = new ShareContentBean();
		inviteFriendsInfoBean.shareTitle =content_info;
		inviteFriendsInfoBean.shareUrl = "http://shizhongapp.com";
		inviteFriendsInfoBean.app_icon_id=R.drawable.sz_shar_image;

	}

	@Override
	protected void initData() {
		mShareManager = new BaseUmengManager();
		mShareManager.addSharePermission(ActivitySettingInviteFriends.this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.options_image:
			ShareItemBean tag = (ShareItemBean) view.getTag(R.string.app_name);
			switch (tag.itemPosition) {
			case 0:
				if (mShareManager != null && this.inviteFriendsInfoBean != null) {
					LogUtils.i("sharebean", "share sina");
					mShareManager.inviteFriends(ActivitySettingInviteFriends.this, SHARE_MEDIA.SINA,
							inviteFriendsInfoBean);
				}

				break;
			case 1:
				if (mShareManager != null && this.inviteFriendsInfoBean != null) {
					LogUtils.i("sharebean", "share winxin");
					mShareManager.inviteFriends(ActivitySettingInviteFriends.this, SHARE_MEDIA.WEIXIN,
							inviteFriendsInfoBean);
				}
				break;
			case 2:
				if (mShareManager != null && this.inviteFriendsInfoBean != null) {
					LogUtils.i("sharebean", "share winxipengyouquan");
					mShareManager.inviteFriends(ActivitySettingInviteFriends.this, SHARE_MEDIA.WEIXIN_CIRCLE,
							inviteFriendsInfoBean);
				}
				break;
			case 3:
				if (mShareManager != null && this.inviteFriendsInfoBean != null) {
					LogUtils.i("sharebean", "share qq");
					mShareManager.inviteFriends(ActivitySettingInviteFriends.this, SHARE_MEDIA.QQ,
							inviteFriendsInfoBean);
				}
				break;
			case 4:
				if (mShareManager != null && this.inviteFriendsInfoBean != null) {
					LogUtils.i("sharebean", "share qzone");
					mShareManager.inviteFriends(ActivitySettingInviteFriends.this, SHARE_MEDIA.QZONE,
							inviteFriendsInfoBean);
				}
				break;
			}
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mShareManager!=null){
			mShareManager=null;
		}
		if(inviteFriendsInfoBean!=null){
			inviteFriendsInfoBean=null;
		}
		if(mAdapter!=null){
			mAdapter=null;
		}
		if(mDatas!=null){
			mDatas.clear();
			mDatas=null;
		}
		if(iconImageId!=null){
			iconImageId=null;
		}
		if(shareIconNames!=null){
			shareIconNames=null;
		}
		System.gc();


	}
}
