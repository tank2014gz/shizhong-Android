package com.shizhong.view.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.RexUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ApplyeClubAlterWindow;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.shizhong.view.ui.base.view.pickview.OptionsPickerView;
import com.shizhong.view.ui.base.view.MenuChoisePhotos;

import com.shizhong.view.ui.bean.CityBean;
import com.shizhong.view.ui.bean.ProvinceBean;
import com.shizhong.view.ui.bean.ZoneBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuliyan on 16/1/28.
 */
public class ActivityClubApply extends BaseFragmentActivity implements View.OnClickListener {

	private SimpleDraweeView mClubLogoView;
	private ClearEditText mClubNameView;
	private ClearEditText mClubPhoneView;
	private TextView mClubAddress1;
	private EditText mClubAddress2;
	private EditText mClubDes;
	private TextView mClubApply;
	private TextView mClubTitle;
	private ImageView mBackImage;

	private String headImageUrl;
	private String login_token;
	private String mClubRegTag = "/club/reg";// 舞社申请
	private String mGetImageTokenTag = "/media/getUpToken";
	private View mRootView;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_club_apply);
		mClubTitle = (TextView) findViewById(R.id.title_tv);
		mClubTitle.setText("申请舞社");
		mBackImage = (ImageView) findViewById(R.id.left_bt);
		mBackImage.setOnClickListener(this);
		mClubLogoView = (SimpleDraweeView) findViewById(R.id.club_logo_image);
		mClubLogoView.setOnClickListener(this);
		mClubNameView = (ClearEditText) findViewById(R.id.club_name);
		mClubPhoneView = (ClearEditText) findViewById(R.id.club_phone);
		mClubAddress1 = (TextView) findViewById(R.id.club_address_1);
		mClubAddress1.setOnClickListener(this);
		mClubAddress2 = (EditText) findViewById(R.id.club_address_2);
		mClubDes = (EditText) findViewById(R.id.club_des);
		mClubApply = (TextView) findViewById(R.id.club_apply);
		mClubApply.setOnClickListener(this);
		mRootView = findViewById(R.id.root_view);
		mRootView.setOnClickListener(this);

	}

	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(ActivityClubApply.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@Override
	protected void initData() {
		initAddressView();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.club_address_1:
			InputMethodUtils.hide(ActivityClubApply.this, view);
			showOrDismissAddress();

			break;
		case R.id.club_logo_image:
			showCameraMenu(view);

			break;
		case R.id.club_apply:

			if (TextUtils.isEmpty(headImageUrl)) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.club_logo_null));
				return;
			}

			String clubName = mClubNameView.getText().toString().trim();
			if (TextUtils.isEmpty(clubName)) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.club_name_null));
			}

			String clubPhone = mClubPhoneView.getText().toString().toString();
			if (TextUtils.isEmpty(clubPhone)) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.phone_null_err));
			}

			if (!clubPhone.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.phone_rex_error));
				return;
			}

			if (mSelectedProvince == null || mSelectedCity == null || mSelectedZone == null) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.error_address_null));
				return;
			}

			String addressName = mClubAddress2.getText().toString().trim();
			if (TextUtils.isEmpty(addressName)) {
				ToastUtils.showShort(ActivityClubApply.this, getString(R.string.club_detail_address_null));
				return;
			}
			String clubDes = mClubDes.getText().toString().trim();
			if (TextUtils.isEmpty(clubDes)) {
				ToastUtils.showShort(ActivityClubApply.this, "简介不能为空");
				return;
			}
			applyCulb(headImageUrl, (mSelectedProvince.ProSort + ""), (mSelectedCity.CitySort + ""),
					(mSelectedZone.ZoneID + ""), addressName, clubName, clubDes, clubPhone);

			break;
		case R.id.root_view:
			InputMethodUtils.hide(ActivityClubApply.this, view);
			break;
		}
	}

	private void applyCulb(String clublogo, String provinceId, String cityId, String districtId, String address,
			String clubName, String clubdes, String phone) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("logoUrl", clublogo);
		params.put("clubName", clubName);
		params.put("description", clubdes);
		params.put("clubContact", phone);
		params.put("provinceId", provinceId);
		params.put("cityId", cityId);
		params.put("districtId", districtId);
		params.put("address", address);
		showLoadingDialog();
		LogUtils.e("申请舞社", "-------");
		BaseHttpNetMananger.getInstance(ActivityClubApply.this).postJSON(ActivityClubApply.this, mClubRegTag, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						//
						try {
							JSONObject dataJSON = new JSONObject(req);
							// 70002异常
							int code = dataJSON.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								showAlterWinow(mClubTitle);
								break;
							case 900001:
								msg = dataJSON.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivityClubApply.this, code, msg, true);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
					}
				}, false);
	}

	private ApplyeClubAlterWindow mAlterWindow;

	private void showAlterWinow(View view) {
		if (mAlterWindow == null) {
			mAlterWindow = new ApplyeClubAlterWindow(ActivityClubApply.this);
		}
		if (!mAlterWindow.isShowing()) {
			mAlterWindow.show(view);
		} else {
			mAlterWindow.dismiss();
		}
	}

	/**
	 * 初始化城市的三级列表
	 */

	private ProvinceBean mSelectedProvince;
	private CityBean mSelectedCity;
	private ZoneBean mSelectedZone;

	@SuppressWarnings("rawtypes")
	private OptionsPickerView mAddressPickerView;
	private ArrayList<ProvinceBean> provinceOptions = new ArrayList<ProvinceBean>();
	private ArrayList<ArrayList<CityBean>> citysOptions = new ArrayList<ArrayList<CityBean>>();
	private ArrayList<ArrayList<ArrayList<ZoneBean>>> zonesOptions = new ArrayList<ArrayList<ArrayList<ZoneBean>>>();

	private MenuChoisePhotos menuChoisePhotos;
	private volatile boolean isFinish = false;
	private AsyncTask<Void, Void, Boolean> getCityTask;
	private PlaceDBManager placeDBManager;

	private void showOrDismissAddress() {
		if (mAddressPickerView != null) {
			if (mAddressPickerView.isShowing()) {
				mAddressPickerView.dismiss();
			} else {
				mAddressPickerView.show();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initAddressView() {
		if (getCityTask == null) {

			// 选项1
			getCityTask = new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					placeDBManager=new PlaceDBManager(ActivityClubApply.this);
					List<ProvinceBean> provinces =placeDBManager.getAllProvince();
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
						citysOptions.add(i, cityBeans);
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
						mAddressPickerView = new OptionsPickerView(ActivityClubApply.this);
						// 三级联动效果
						mAddressPickerView.setPicker(provinceOptions, citysOptions, zonesOptions, true);
						// 设置选择的三级单位
						// pwOptions.setLabels("省", "市", "区");
						mAddressPickerView.setTitle("选择城市");
						mAddressPickerView.setCyclic(false, true, true);
						// 设置默认选中的三级项目
						// 监听确定选择按钮
						mAddressPickerView.setSelectOptions(1, 0, 0);
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
								mClubAddress1.setText(address);
							}
						});
					}
				};

			}.execute();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (menuChoisePhotos != null) {
			menuChoisePhotos.onActivityResult(ActivityClubApply.this, requestCode, resultCode, data);
		}
	}

	private void showCameraMenu(View view) {
		if (menuChoisePhotos == null) {
			menuChoisePhotos = new MenuChoisePhotos(ActivityClubApply.this);
			menuChoisePhotos.setCut(true);
			menuChoisePhotos.setCallBack(new MenuChoisePhotos.PhotoCallBack() {
				@SuppressWarnings("deprecation")
				@Override
				public void callPhoto(Object photos) {
					// ToastUtils.showShort(RegFinishUserInfoActivity.this,photos.toString());
					File file = (File) photos;
					mClubLogoView.setImageURI(FileUtils.getFileUri(file.getPath()));
					// 七牛上传头像
					// num1:请求上传koken
					showLoadingDialog();
					getImageUploadToken(login_token, BaseQiNiuRequest.MODEL_ACTION_CLUB_LOGO, file);// 上传舞社logo

				}
			});
		}
		if (!menuChoisePhotos.isShowing()) {
			menuChoisePhotos.show(view);
		}
	}

	private void getImageUploadToken(String login_token, String type, final File file) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", login_token);
		LogUtils.e("获取图片token", "-------");
		BaseHttpNetMananger.getInstance(ActivityClubApply.this).postJSON(ActivityClubApply.this, mGetImageTokenTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject dataJSON = new JSONObject(req);
							int code = dataJSON.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								String up_token = dataJSON.getString("data");
								// 获取uptoken之后上传到七牛服务器
								uploadHeadImage(up_token, file);

								break;
							case 900001:
								msg = dataJSON.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivityClubApply.this, code, msg, true);
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							dismissLoadingDialog();
							ToastUtils.showShort(ActivityClubApply.this, getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityClubApply.this, getString(R.string.net_error));
						dismissLoadingDialog();

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityClubApply.this, getString(R.string.net_error));
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
					} catch (JSONException e) {
						e.printStackTrace();
						ToastUtils.showShort(ActivityClubApply.this, getString(R.string.error_jsonpare));
					}

				} else {
					ToastUtils.showShort(ActivityClubApply.this, getString(R.string.error_member_head));
				}

			}
		}, null);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isFinish = true;
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(mClubRegTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(mGetImageTokenTag);
		}
		if (getCityTask != null) {
			getCityTask.cancel(true);
			getCityTask = null;
		}
		if (provinceOptions != null) {
			provinceOptions = null;
		}
		if (citysOptions != null) {
			citysOptions = null;
		}
		if (zonesOptions != null) {
			zonesOptions = null;
		}
		if (mAddressPickerView != null) {
			mAddressPickerView = null;
		}
		if (menuChoisePhotos != null) {
			menuChoisePhotos = null;
		}

		System.gc();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (menuChoisePhotos != null) {
			menuChoisePhotos.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
