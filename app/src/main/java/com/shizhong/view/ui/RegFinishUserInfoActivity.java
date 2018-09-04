package com.shizhong.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.ImageCacheManager;
import com.shizhong.view.ui.base.utils.ImageCacheManager.ImageCacheListener;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.shizhong.view.ui.base.view.MyRadioGroup;
import com.shizhong.view.ui.base.view.pickview.OptionsPickerView;
import com.shizhong.view.ui.base.view.pickview.TimePickerView;
import com.shizhong.view.ui.base.view.MenuChoisePhotos;
import com.shizhong.view.ui.bean.CityBean;
import com.shizhong.view.ui.bean.ProvinceBean;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.bean.ZoneBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuliyan on 15/12/26.
 */
public class RegFinishUserInfoActivity extends BaseUmengActivity
		implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
	private final String TAG = "RegFinishUserInfoActivity";
	private SimpleDraweeView mRoundImageView;
	private ClearEditText mUserNickName;
	private TextView mUserAddress;
	private RadioGroup mUserGender;
	private TextView mNextBt;
	private TextView mAgreement;
	private TextView mHeadDes;
	private TextView mBirthText;
	private byte isMen = 1;// 是否是男士
	private String login_token;
	private String headImageUrl;
	private ProvinceBean mSelectedProvince;
	private CityBean mSelectedCity;
	private ZoneBean mSelectedZone;
	private String nickName;
	private String headUrl;
	private String sex;

	private String user_birth;
	private String getImageTokenTag = "/media/getUpToken";
	private String getModifyInfoTag = "/member/modifyMemberInfo";
	private boolean isFailedFinishUserInfo;


	private int login_style;//注册登录平台方式
	private  final  int go_to_next_action=0x1021;

	/**
	 * 初始化城市的三级列表
	 */
	@SuppressWarnings("rawtypes")
	private OptionsPickerView mAddressPickerView;
	private ArrayList<ProvinceBean> provinceOptions = new ArrayList<ProvinceBean>();
	private ArrayList<ArrayList<CityBean>> citysOptions = new ArrayList<ArrayList<CityBean>>();
	private ArrayList<ArrayList<ArrayList<ZoneBean>>> zonesOptions = new ArrayList<ArrayList<ArrayList<ZoneBean>>>();

	private TimePickerView mTimePickerView;
	private volatile boolean isFinish = false;


	@Override
	protected void initBundle() {
		isFailedFinishUserInfo=getIntent().getBooleanExtra(ContantsActivity.LoginModle.IS_USER_FINISH_FAIL,false);
		login_token = PrefUtils.getString(RegFinishUserInfoActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		if (TextUtils.isEmpty(login_token)) {
			ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.token_null));
		}
		login_style=PrefUtils.getInt(RegFinishUserInfoActivity.this,ContantsActivity.LoginModle.LOGIN_PLATFORM, LoginActivity.ACTION_LOGIN_STYLE_PHONE);

           switch (login_style){
			   case LoginActivity.ACTION_LOGIN_STYLE_QQ:
			   case LoginActivity.ACTION_LOGIN_STYLE_WEIXIN:
			   case LoginActivity.ACTION_LOGIN_STYLE_WEIBO:
				   nickName=PrefUtils.getString(RegFinishUserInfoActivity.this, ContantsActivity.LoginModle.NICK_NAME,"");
				   sex=PrefUtils.getString(RegFinishUserInfoActivity.this, ContantsActivity.LoginModle.SEX,"1");
				   headUrl=PrefUtils.getString(RegFinishUserInfoActivity.this,ContantsActivity.LoginModle.HEAD_URL,"");
				   break;
		   }
	}
	@Override
	protected void initView() {
		setContentView(R.layout.activity_reg);
		mRoundImageView = (SimpleDraweeView) findViewById(R.id.user_head);
		mRoundImageView.setOnClickListener(this);
		mUserNickName = (ClearEditText) findViewById(R.id.nick_name);
		mUserGender = (MyRadioGroup) findViewById(R.id.gender);
		mUserGender.setOnCheckedChangeListener(this);
		mAgreement = (TextView) findViewById(R.id.user_agreement);
		mUserAddress = (TextView) findViewById(R.id.address_edit);
		mUserAddress.setOnClickListener(this);
		mBirthText = (TextView) findViewById(R.id.birth_edit);
		mBirthText.setOnClickListener(this);
		mAgreement.setOnClickListener(this);
		mNextBt = (TextView) findViewById(R.id.next);
		mNextBt.setOnClickListener(this);
		mHeadDes = (TextView) findViewById(R.id.photo_des);


	}


	@Override
	protected void initData() {

		switch (login_style){
			case LoginActivity.ACTION_LOGIN_STYLE_PHONE:
				mHeadDes.setVisibility(View.VISIBLE);
				break;
			case LoginActivity.ACTION_LOGIN_STYLE_WEIXIN:
			case LoginActivity.ACTION_LOGIN_STYLE_QQ:
			case LoginActivity.ACTION_LOGIN_STYLE_WEIBO:
				mUserNickName.setText(nickName);
				if ("1".equals(sex)) {
					mUserGender.check(R.id.men);
				} else if ("2".equals(sex)) {
					mUserGender.check(R.id.women);
				}
				mHeadDes.setVisibility(View.GONE);
				ImageCacheManager.downLoadImage(RegFinishUserInfoActivity.this, headUrl, imageCacheListener);
				break;
		}
		initAddressView();
		initBirthView();



	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.user_head:
			showCameraMenu(view);
			break;
		case R.id.user_agreement:
			skipNextAgrree();
			break;
		case R.id.next:
			nickName = mUserNickName.getText().toString().trim();
			user_birth = mBirthText.getText().toString().trim();
			if (TextUtils.isEmpty(nickName)) {
				ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.nick_name_null_err));
				return;
			}
			if (TextUtils.isEmpty(user_birth)) {
				ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.birth_error_null));
				return;
			}
			if (mSelectedProvince == null || mSelectedCity == null || mSelectedZone == null) {
				ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.error_address_null));
				return;
			}

			if(FileUtils.checkFile(mHeadImageCacheFile)){
				getImageUploadToken(login_token, BaseQiNiuRequest.MODEL_ACTION_MEMBER_HEAD, mHeadImageCacheFile);
			}else {
				ToastUtils.showShort(RegFinishUserInfoActivity.this, "请选择头像");
			}

			break;
		case R.id.address_edit:
			showOrDismissAddress();
			break;
		case R.id.birth_edit:
			showOrDismissBirthView();
			break;
		}
	}

	private void skipNextAgrree() {
		mIntent.setClass(RegFinishUserInfoActivity.this, AgreementActivity.class);
		mIntent.putExtra(ContantsActivity.WEB.TITLE,"失重用户协议");
		mIntent.putExtra(ContantsActivity.WEB.URL,"http://7xr57b.dl1.z0.glb.clouddn.com/yonghuxieyi.htm");
		startActivityForResult(mIntent, go_to_next_action);
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {
		switch (i) {
		case R.id.men:
			isMen = 1;
			break;
		case R.id.women:
			isMen = 0;
			break;
		}
	}

	private void commiteUserInfo(String login_token,String headImageUrl,final  String nickName,String user_birth,String sex,String pro,String city,String zone) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("headerUrl", headImageUrl);
		params.put("nickname", nickName);
		params.put("birthday", user_birth);
		params.put("sex", sex);
		params.put("provinceId",pro);
		params.put("cityId",city);
		params.put("districtId",zone);
		LogUtils.i(TAG, "member params:" + params);
		showLoadingDialog();
		LogUtils.e("修改用户资料", "-------");
		BaseHttpNetMananger.getInstance(RegFinishUserInfoActivity.this).postJSON(RegFinishUserInfoActivity.this,
				getModifyInfoTag, params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								ToastUtils.showShort(RegFinishUserInfoActivity.this, "修改成功");
								PrefUtils.putString(RegFinishUserInfoActivity.this,
										ContantsActivity.LoginModle.NICK_NAME, nickName);

								skipNext();
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(RegFinishUserInfoActivity.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.net_conected_error));

					}
				}, false);
	}

	private void skipNext() {
		mIntent.setClass(RegFinishUserInfoActivity.this, DanceClassChoiseActivity.class);
		startActivityForResult(mIntent, go_to_next_action);
	}





	private MenuChoisePhotos menuChoisePhotos;

	private void showCameraMenu(View view) {
		if (menuChoisePhotos == null) {
			menuChoisePhotos = new MenuChoisePhotos(RegFinishUserInfoActivity.this);
			menuChoisePhotos.setCut(true);
			menuChoisePhotos.setCallBack(new MenuChoisePhotos.PhotoCallBack() {
				@Override
				public void callPhoto(Object photos) {
					mHeadImageCacheFile = (File) photos;
					String path = mHeadImageCacheFile.getAbsolutePath();
					mRoundImageView.setImageURI(FileUtils.getFileUri(path));
					mHeadDes.setVisibility(View.GONE);

				}
			});
		}
		if (!menuChoisePhotos.isShowing()) {
			menuChoisePhotos.show(view);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (menuChoisePhotos != null) {
			menuChoisePhotos.onActivityResult(RegFinishUserInfoActivity.this, requestCode, resultCode, data);
		}
		LogUtils.e("shizhong","120000:--------"+requestCode+"----"+requestCode);
		switch (requestCode){
			case go_to_next_action:
				if(resultCode==ContantsActivity.Action.RESULT_ACTION_REG_SUCCESS){
					if(!isFailedFinishUserInfo){
						setResult(resultCode);
					}else {
						mIntent.setClass(RegFinishUserInfoActivity.this, MainActivity.class);
						PrefUtils.putBoolean(RegFinishUserInfoActivity.this, ContantsActivity.LoginModle.IS_LOGIN, true);
						startActivity(mIntent);
					}
					super.onBackPressed();
				}
				break;
		}

	}

	private void getImageUploadToken(String login_token, String type, final File file) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", login_token);
		showLoadingDialog();
		LogUtils.e("获取图片token", "-------");
		BaseHttpNetMananger.getInstance(RegFinishUserInfoActivity.this).postJSON(RegFinishUserInfoActivity.this,
				getImageTokenTag, params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								String up_token = root.getString("data");
								LogUtils.i(TAG, "获取上传token成功" + up_token);
								// 获取uptoken之后上传到七牛服务器
								uploadHeadImage(up_token, file);

								break;
							case 900001:
								msg = root.getString("msg");
							default:
								dismissLoadingDialog();
								ToastUtils.showErrorToast(RegFinishUserInfoActivity.this, code, msg, true);
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							dismissLoadingDialog();
							ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.net_error));
						dismissLoadingDialog();

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.net_error));
						dismissLoadingDialog();
					}
				}, false);
	}

	private UploadManager mUploadHeadManager;

	private void uploadHeadImage(String up_token, File file) {
		if (this.mUploadHeadManager == null) {
			this.mUploadHeadManager = new UploadManager();
		}

		showLoadingDialog();
		this.mUploadHeadManager.put(file, BaseQiNiuRequest.getImageFileKey(), up_token, new UpCompletionHandler() {
			@Override
			public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
				dismissLoadingDialog();
				if (responseInfo.isOK()) {
					try {
						String fileKey = jsonObject.getString("key");
						headImageUrl = fileKey;
						commiteUserInfo(login_token,headImageUrl,nickName,user_birth,isMen+"", mSelectedProvince.ProSort+"", mSelectedCity.CitySort + "", mSelectedZone.ZoneID + "");

					} catch (JSONException e) {
						e.printStackTrace();
						ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.error_jsonpare));
					}

				} else {
					ToastUtils.showShort(RegFinishUserInfoActivity.this, getString(R.string.error_member_head));
				}

			}
		}, null);

	}

	@Override
	public void onBackPressed() {
		ToastUtils.showShort(RegFinishUserInfoActivity.this, "必须完善个人资料");
	}

	private File mHeadImageCacheFile;

	private ImageCacheListener imageCacheListener = new ImageCacheListener() {

		@Override
		public void success(File file) {
			if (file != null && file.exists())
				mHeadImageCacheFile = file;
			if (!isFinish && mMainHandler != null) {
				mMainHandler.post(new Runnable() {

					@Override
					public void run() {
						if (mRoundImageView != null) {
							mRoundImageView.setImageURI(FileUtils.getFileUri(mHeadImageCacheFile.getAbsolutePath()));
						}

					}
				});
			}

		}

		@Override
		public void fail(final String reasion) {
			mMainHandler.post(new Runnable() {
				public void run() {
					ToastUtils.showShort(RegFinishUserInfoActivity.this, reasion);
				}
			});

		}
	};
	private void initBirthView() {
		if (mTimePickerView == null) {
			mTimePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
			mTimePickerView.setTime(new Date());
			mTimePickerView.setRange(1973, 2016);
			mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
				@Override
				public void onTimeSelect(Date date) {
					String birth = DateUtils.format1(date);
					mBirthText.setText(birth);
				}
			});
		}
	}

	private void showOrDismissBirthView() {
		if (mTimePickerView.isShowing()) {
			mTimePickerView.dismiss();
		} else {
			mTimePickerView.show();
		}
	}

	private AsyncTask<Void, Void, Boolean> getCityTask = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initAddressView() {
		if (getCityTask == null) {
			getCityTask = new AsyncTask<Void, Void, Boolean>() {
				private  PlaceDBManager placeDBManager=new PlaceDBManager(RegFinishUserInfoActivity.this);
				@Override
				protected Boolean doInBackground(Void... params) {
					List<ProvinceBean> provinces = placeDBManager
							.getAllProvince();
					if (provinces != null) {
						provinceOptions.clear();
						provinceOptions.addAll(provinces);
					}
					int len = provinceOptions.size();
					ArrayList<CityBean> cityBeans = null;
					for (int i = 0; i < len; i++) {
						cityBeans = new ArrayList<CityBean>();
						int proId = provinceOptions.get(i).ProSort;
						if (i + 1 == proId) {
							cityBeans.addAll(placeDBManager.getCity(proId));
						}
						if (!citysOptions.contains(cityBeans)) {
							citysOptions.add(i, cityBeans);
						}
					}
					zonesOptions.clear();
					ArrayList<ArrayList<ZoneBean>> zoneItems = null;
					for (int i = 0; i < len; i++) {
						zoneItems = new ArrayList<ArrayList<ZoneBean>>();
						int m = citysOptions.get(i).size();
						ArrayList<ZoneBean> zoneBeans = null;
						for (int j = 0; j < m; j++) {
							int cityID = citysOptions.get(i).get(j).CitySort;
							zoneBeans = (ArrayList<ZoneBean>) placeDBManager
									.getZone(cityID);
							zoneItems.add(zoneBeans);
						}
						zonesOptions.add(i, zoneItems);
					}
					return true;
				}

				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result && !isFinish && mAddressPickerView == null) {
						mAddressPickerView = new OptionsPickerView(RegFinishUserInfoActivity.this);
						// 三级联动效果
						mAddressPickerView.setPicker(provinceOptions, citysOptions, zonesOptions, true);
						// 设置选择的三级单位
						// pwOptions.setLabels("省", "市", "区");
						mAddressPickerView.setTitle("选择城市");
						mAddressPickerView.setCyclic(false, true, true);
						// 设置默认选中的三级项目
						// 监听确定选择按钮
						mAddressPickerView.setSelectOptions(0, 0, 0);
						mAddressPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

							@Override
							public void onOptionsSelect(int options1, int option2, int options3) {
								// 返回的分别是三个级别的选中位置
								mSelectedProvince = provinceOptions.get(options1);
								mSelectedCity = citysOptions.get(options1).get(option2);
								mSelectedZone = zonesOptions.get(options1).get(option2).get(options3);
								String provice = provinceOptions.get(options1).ProName;
								String city = citysOptions.get(options1).get(option2).toString();
								String zone = zonesOptions.get(options1).get(option2).get(options3).toString();
								StringBuilder address = new StringBuilder();
								if (provice.contains(city)) {
									address.append(provice);
								} else {
									address.append(provice).append("_").append(city);
								}
								if (!TextUtils.isEmpty(zone)) {
									address.append("_").append(zone);
								}
								mUserAddress.setText(address);
							}
						});

					}

				};

			}.execute();
		}
	}

	private void showOrDismissAddress() {
		if (mAddressPickerView != null)
			if (mAddressPickerView.isShowing()) {
				mAddressPickerView.dismiss();
			} else {
				mAddressPickerView.show();
			}
	}




	@Override
	public void onRequestPermissionsResult(int requestcode, String[] permissions, int[] grantResult) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestcode, permissions, grantResult);
		if (menuChoisePhotos != null) {
			menuChoisePhotos.onRequestPermissionsResult(requestcode, permissions, grantResult);
		}

	}

	@Override
	protected void onDestroy() {
		isFinish = true;
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getModifyInfoTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getImageTokenTag);
		}
		if (getCityTask != null) {
			getCityTask.cancel(true);
			getCityTask = null;
		}

		if (mSelectedProvince != null) {
			mSelectedProvince = null;
		}
		if (mSelectedCity != null) {
			mSelectedCity = null;
		}
		if (mSelectedZone != null) {
			mSelectedZone = null;
		}
		if (provinceOptions != null) {
			provinceOptions.clear();
			provinceOptions = null;
		}
		if (citysOptions != null) {
			citysOptions.clear();
			citysOptions = null;
		}
		if (zonesOptions != null) {
			zonesOptions.clear();
			zonesOptions = null;
		}

		if (mAddressPickerView != null) {
			mAddressPickerView = null;
		}
		if (mTimePickerView != null) {
			mTimePickerView = null;
		}

		if (mMainHandler != null) {
			mMainHandler = null;
		}
		if (mHeadImageCacheFile != null) {
			mHeadImageCacheFile = null;
		}
		if (mUploadHeadManager != null) {
			mUploadHeadManager = null;
		}
		if (menuChoisePhotos != null) {
			menuChoisePhotos = null;
		}

		System.gc();
	};

}
