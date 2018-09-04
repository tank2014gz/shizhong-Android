package com.shizhong.view.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.MenuChoisePhotos;
import com.shizhong.view.ui.base.view.MenuReportUser;
import com.shizhong.view.ui.base.view.pickview.OptionsPickerView;
import com.shizhong.view.ui.base.view.pickview.TimePickerView;
import com.shizhong.view.ui.bean.CityBean;
import com.shizhong.view.ui.bean.ProvinceBean;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.ZoneBean;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityUserModify extends BaseFragmentActivity implements OnClickListener, OnPreDrawListener {
	private static final String TAG = "ActivityUserModify";
	private ImageView mReportBtn;
	private TextView mFinishBtn;
	private SimpleDraweeView mMemberHead;
	private boolean isMemberHeadModify;
	private String login_token;
	private String member_id;
	private View mMemberNickNameView;
	private TextView mMemberNickName;
	private String nickname;
	private boolean isNickNameModify;
	private TextView mMemberSex;
	private TextView mMemberBirthDay;
	private View mMemberBirthDayView;
	private boolean isBirthModify;
	private String birth;
	private TextView mMemberConstellation;// 星座
	private TextView mMemberPlace;
	private View mMemberPlaceView;

	private  PlaceDBManager placeDBManager;
	private TextView mMemberSignture;
	private View mMembeSigntureView;
	private String signture;
	private boolean isSingntureModify;

	private String headImageUrl;

	private ProvinceBean mSelectedProvince;
	private CityBean mSelectedCity;
	private ZoneBean mSelectedZone;
	private boolean isModifyPlace;
	private String user_id;
	private boolean isMe;
	private String getModifyUserInfoTag = "/member/modifyMemberInfo";
	private volatile boolean isFinish = false;
	/**
	 * 初始化城市的三级列表
	 */
	@SuppressWarnings("rawtypes")
	private OptionsPickerView mAddressPickerView;
	private ArrayList<ProvinceBean> provinceOptions = new ArrayList<ProvinceBean>();
	private ArrayList<ArrayList<CityBean>> citysOptions = new ArrayList<ArrayList<CityBean>>();
	private ArrayList<ArrayList<ArrayList<ZoneBean>>> zonesOptions = new ArrayList<ArrayList<ArrayList<ZoneBean>>>();

	private UserExtendsInfo mUserInfo;
	private AsyncTask<Void, Void, Boolean> getCityInfoTask = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initAddressView() {
		if (getCityInfoTask == null) {
			getCityInfoTask = new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {

					List<ProvinceBean> provinces = placeDBManager.getAllProvince();
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

				@Override
				protected void onPostExecute(Boolean result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (result && !isFinish) {
						if (mAddressPickerView == null) {
							mAddressPickerView = new OptionsPickerView(ActivityUserModify.this);
							// 选项1
							// 三级联动效果
							mAddressPickerView.setPicker(provinceOptions, citysOptions, zonesOptions, true);
							// 设置选择的三级单位
							// pwOptions.setLabels("省", "市", "区");
							mAddressPickerView.setTitle("选择城市");
							mAddressPickerView.setCyclic(false, true, true);
							// 设置默认选中的三级项目
							// 监听确定选择按钮
							mAddressPickerView.setSelectOptions(1, 0, 0);
							mAddressPickerView
									.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

										@Override
										public void onOptionsSelect(int options1, int option2, int options3) {
											isModifyPlace = true;
											// 返回的分别是三个级别的选中位置
											mSelectedProvince = provinceOptions.get(options1);
											mSelectedCity = citysOptions.get(options1).get(option2);
											mSelectedZone = zonesOptions.get(options1).get(option2).get(options3);
											String provice = provinceOptions.get(options1).ProName;
											String city = citysOptions.get(options1).get(option2).toString();
											String zone = zonesOptions.get(options1).get(option2).get(options3)
													.toString();
											StringBuilder address = new StringBuilder();
											if (provice.contains(city)) {
												address.append(provice);
											} else {
												address.append(provice).append("_").append(city);
											}
											if (!TextUtils.isEmpty(zone)) {
												address.append("_").append(zone);
											}
											mMemberPlace.setText(address);
										}
									});
						}
					}
				}

			}.execute();
		}
	}

	private void showOrDismissAddress() {
		if (mAddressPickerView.isShowing()) {
			mAddressPickerView.dismiss();
		} else {
			mAddressPickerView.show();
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_user_modify_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("个人资料");
		mFinishBtn = (TextView) findViewById(R.id.right_tv);
		mFinishBtn.setText("保存");

		mReportBtn = (ImageView) findViewById(R.id.right_iv);
		mReportBtn.setImageResource(R.drawable.sz_report);

		if (user_id.equals(member_id)) {
			mFinishBtn.setVisibility(View.VISIBLE);
			mReportBtn.setVisibility(View.GONE);
			mFinishBtn.setOnClickListener(this);
		} else {
			mFinishBtn.setVisibility(View.GONE);
			mReportBtn.setVisibility(View.VISIBLE);
			mReportBtn.setOnClickListener(this);
		}

		mMemberHead = (SimpleDraweeView) findViewById(R.id.member_head);
		mMemberNickName = (TextView) findViewById(R.id.eidt_nick_name);
		mMemberNickNameView = findViewById(R.id.nickname_layout);

		mMemberSex = (TextView) findViewById(R.id.eidt_gender);
		mMemberBirthDay = (TextView) findViewById(R.id.eidt_birth);
		mMemberBirthDayView = findViewById(R.id.birth_layout);
		mMemberConstellation = (TextView) findViewById(R.id.eidt_constellation);
		mMemberPlace = (TextView) findViewById(R.id.eidt_place);
		mMemberPlaceView = findViewById(R.id.place_layout);

		mMemberSignture = (TextView) findViewById(R.id.eidt_signture);
		mMembeSigntureView = findViewById(R.id.signture_layout);
		if (isMe) {
			mMemberHead.setOnClickListener(this);
			mMemberNickNameView.setOnClickListener(this);
			mMemberPlaceView.setOnClickListener(this);
			mMembeSigntureView.setOnClickListener(this);
			mMemberBirthDayView.setOnClickListener(this);
			mMemberConstellation.setOnClickListener(this);
		}
	}

	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(ActivityUserModify.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mUserInfo = (UserExtendsInfo) getIntent().getSerializableExtra("user_info");
		member_id = getIntent().getStringExtra(ContantsActivity.LoginModle.LOGIN_USER_ID);
		user_id = PrefUtils.getString(ActivityUserModify.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		isMe = member_id.equals(user_id);
		placeDBManager=new PlaceDBManager(ActivityUserModify.this);

	}

	@Override
	protected void initData() {

		if (mUserInfo == null) {
			requestMemberInfo();
		} else {
			fillInfo(mUserInfo);
		}

	}

	private void requestMemberInfo() {
		VideoHttpRequest.getMememberInfo("/member/getMember", ActivityUserModify.this, login_token, member_id, isMe,
				new VideoHttpRequest.HttpRequestCallBack1() {
					@Override
					public void callBack(UserExtendsInfo info) {
						fillInfo(info);
					}

					@Override
					public void callBackFail() {
						// TODO Auto-generated method stub

					}
				});
	}

	private void showAlertDialog() {
		Dialog alertDialog = DialogUtils.confirmDialog(ActivityUserModify.this, "是否放弃修改", "保存", "放弃",
				new ConfirmDialog() {

					@Override
					public void onOKClick(Dialog dialog) {
						submitModify();
					}

					@Override
					public void onCancleClick(Dialog dialog) {
						onBackPressed();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			if (isBirthModify || isMemberHeadModify || isModifyPlace || isSingntureModify || isNickNameModify) {
				showAlertDialog();
			} else {
				onBackPressed();
			}

			break;
		case R.id.right_tv:

			submitModify();

			break;
		case R.id.right_iv:
			if (!TextUtils.isEmpty(member_id)) {
				showReportWindow(member_id, v);
			} else {

			}
			break;
		case R.id.member_head:
			showCameraMenu(v);
			break;
		case R.id.nickname_layout:
			String nickName = mMemberNickName.getText().toString();
			if (TextUtils.isEmpty(nickName)) {
				nickName = "";
			}
			mIntent.setClass(ActivityUserModify.this, ActivityModifyNickName.class);
			mIntent.putExtra(ContantsActivity.LoginModle.NICK_NAME, nickName);
			startActivityForResult(mIntent, ContantsActivity.Action.ACTION_MODIFY_NICKNAME);
			break;
		case R.id.birth_layout:
		case R.id.eidt_constellation:
			showOrDismissBirthView();
			break;

		case R.id.signture_layout:
			String sigture = mMemberSignture.getText().toString();
			if (TextUtils.isEmpty(sigture)) {
				sigture = "";
			}
			mIntent.setClass(ActivityUserModify.this, ActivityModifySignture.class);
			mIntent.putExtra(ContantsActivity.LoginModle.SIGNTURE, sigture);
			startActivityForResult(mIntent, ContantsActivity.Action.ACTION_MODIFY_SIGNTURE);

			break;
		case R.id.place_layout:
			showOrDismissAddress();
			break;
		}

	}

	private MenuReportUser mMenuReport;

	private void showReportWindow(String memberId, View view) {
		if (mMenuReport == null) {
			mMenuReport = new MenuReportUser(memberId, ActivityUserModify.this);
		}
		if (!mMenuReport.isShowing()) {
			mMenuReport.show(view);
		} else {
			mMenuReport.dismiss();
		}
	}

	private TimePickerView mTimePickerView;

	private void initBirthView() {
		if (mTimePickerView == null) {
			mTimePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
			mTimePickerView.setTime(new Date());
			mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
				@Override
				public void onTimeSelect(Date date) {
					isBirthModify = true;
					String birth = DateUtils.format1(date);
					mMemberBirthDay.setText(birth);
					mMemberConstellation.setText(DateUtils.getConstellation(birth));
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

	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		super.onActivityResult(req, res, data);
		if (menuChoisePhotos != null) {
			menuChoisePhotos.onActivityResult(ActivityUserModify.this, req, res, data);
		}
		if (res == Activity.RESULT_OK) {
			switch (req) {
			case ContantsActivity.Action.ACTION_MODIFY_NICKNAME:

				String nickName = data.getStringExtra(ContantsActivity.LoginModle.NICK_NAME);
				if (!nickName.equals(mMemberNickName.getText().toString())) {
					isNickNameModify = true;
					mMemberNickName.setText(nickName);
				}

				break;
			case ContantsActivity.Action.ACTION_MODIFY_SIGNTURE:
				String signture = data.getStringExtra(ContantsActivity.LoginModle.SIGNTURE);
				if (!mMemberSignture.getText().toString().equals(signture)) {
					isSingntureModify = true;
					mMemberSignture.setText(signture);
				}

			default:
				break;
			}
		} else if (res == Activity.RESULT_CANCELED) {

		}

	}

	private MenuChoisePhotos menuChoisePhotos;

	private void showCameraMenu(View view) {
		if (menuChoisePhotos == null) {
			menuChoisePhotos = new MenuChoisePhotos(ActivityUserModify.this);
			menuChoisePhotos.setCut(true);
			menuChoisePhotos.setCallBack(new MenuChoisePhotos.PhotoCallBack() {
				@SuppressWarnings("deprecation")
				@Override
				public void callPhoto(Object photos) {
					// ToastUtils.showShort(RegFinishUserInfoActivity.this,photos.toString());
					File file = (File) photos;
					mMemberHead.setImageURI(FileUtils.getFileUri(file.getAbsolutePath()));
//					mMemberHead.setImageDrawable(image);
					// mMemberHeadBack.setImageDrawable(image);
					// 七牛上传头像
					// num1:请求上传koken
					showLoadingDialog();
					getImageUploadToken(login_token, BaseQiNiuRequest.MODEL_ACTION_MEMBER_HEAD, file);

				}
			});
		}
		if (!menuChoisePhotos.isShowing()) {
			menuChoisePhotos.show(view);
		}
	}

	private void getImageUploadToken(String login_token, String type, final File file) {
		String baseURL = "/media/getUpToken";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", login_token);
		LogUtils.e("获取图片token", "-------");
		BaseHttpNetMananger.getInstance(ActivityUserModify.this).postJSON(ActivityUserModify.this, baseURL, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								String up_token = root.getString("data");
								uploadHeadImage(up_token, file);
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								dismissLoadingDialog();
								ToastUtils.showErrorToast(ActivityUserModify.this, code, msg, true);
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							dismissLoadingDialog();
							ToastUtils.showShort(ActivityUserModify.this, getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityUserModify.this, getString(R.string.net_error));
						dismissLoadingDialog();

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityUserModify.this, getString(R.string.net_error));
						dismissLoadingDialog();
					}
				}, false);
	}

	private UploadManager mUploadHeadManager;

	private void uploadHeadImage(String up_token, File file) {
		if (this.mUploadHeadManager == null) {
			this.mUploadHeadManager = new UploadManager();
		}

		UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
			@Override
			public void progress(String s, final double percent) {

			}
		}, null);
		showLoadingDialog();
		this.mUploadHeadManager.put(file, BaseQiNiuRequest.getImageFileKey(), up_token, new UpCompletionHandler() {
			@Override
			public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {

				dismissLoadingDialog();
				isMemberHeadModify = true;
				if (responseInfo.isOK()) {
					try {
						String fileKey = jsonObject.getString("key");
						headImageUrl = fileKey;
					} catch (JSONException e) {
						e.printStackTrace();
						ToastUtils.showShort(ActivityUserModify.this, getString(R.string.error_jsonpare));
					}

				} else {
					ToastUtils.showShort(ActivityUserModify.this, getString(R.string.error_member_head));
				}

			}
		}, uploadOptions);

	}

	private void submitModify() {
		Map<String, String> params = new HashMap<String, String>();
		if (isMemberHeadModify) {
			if (TextUtils.isEmpty(headImageUrl)) {
				ToastUtils.showShort(ActivityUserModify.this, getString(R.string.error_photo_null));
				return;
			}
			params.put("headerUrl", headImageUrl);
		}
		if (isNickNameModify) {
			nickname = mMemberNickName.getText().toString();
			if (TextUtils.isEmpty(nickname)) {
				ToastUtils.showShort(ActivityUserModify.this, getString(R.string.nick_name));
				return;
			}

			params.put("nickname", nickname);
		}
		if (isBirthModify) {
			birth = mMemberBirthDay.getText().toString().trim();
			if (TextUtils.isEmpty(birth)) {
				ToastUtils.showShort(ActivityUserModify.this, getString(R.string.birth_error_null));
				return;
			}
			params.put("birthday", birth);
		}
		if (isModifyPlace) {
			if (mSelectedProvince == null || mSelectedCity == null || mSelectedZone == null) {
				ToastUtils.showShort(ActivityUserModify.this, getString(R.string.error_address_null));
				return;
			}
			params.put("provinceId", mSelectedProvince.ProSort + "");
			params.put("cityId", mSelectedCity.CitySort + "");
			params.put("districtId", mSelectedZone.ZoneID + "");
		}
		if (isSingntureModify) {
			signture = mMemberSignture.getText().toString();
			if (TextUtils.isEmpty(signture)) {
				signture = "";
			}
			params.put("signature", signture);
		}

		if (params == null || params.size() <= 0) {
			ToastUtils.showShort(ActivityUserModify.this, "暂时没做任何更改");
			return;
		}

		params.put("token", login_token);
		showLoadingDialog();
		LogUtils.e("修改个人资料", "-------");
		BaseHttpNetMananger.getInstance(ActivityUserModify.this).postJSON(ActivityUserModify.this, getModifyUserInfoTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								ToastUtils.showShort(ActivityUserModify.this, "修改成功");
								if (isMemberHeadModify) {
									PrefUtils.putString(ActivityUserModify.this, ContantsActivity.LoginModle.HEAD_URL,
											headImageUrl);
									isMemberHeadModify = false;
								}
								if (isNickNameModify) {
									PrefUtils.putString(ActivityUserModify.this, ContantsActivity.LoginModle.NICK_NAME,
											nickname);
									isNickNameModify = false;
								}
								if (isBirthModify) {
									PrefUtils.putString(ActivityUserModify.this, ContantsActivity.LoginModle.BIRTHDAY,
											birth);
									isBirthModify = false;
								}
								if (isModifyPlace) {
									PrefUtils.putInt(ActivityUserModify.this, ContantsActivity.LoginModle.PROVINCE,
											mSelectedProvince.ProSort);
									PrefUtils.putInt(ActivityUserModify.this, ContantsActivity.LoginModle.CITY,
											mSelectedCity.CitySort);
									PrefUtils.putInt(ActivityUserModify.this, ContantsActivity.LoginModle.DISTRICTID,
											mSelectedZone.ZoneID);
									isModifyPlace = false;
								}

								if (isSingntureModify) {
									PrefUtils.putString(ActivityUserModify.this, ContantsActivity.LoginModle.SIGNTURE,
											signture);
									isSingntureModify = false;
								}
								setResult(Activity.RESULT_OK);
								finish();
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivityUserModify.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						dismissLoadingDialog();

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(ActivityUserModify.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(ActivityUserModify.this, getString(R.string.net_conected_error));

					}
				}, false);
	}

	@Override
	public boolean onPreDraw() {
		// mMemberHeadBack.getViewTreeObserver().removeOnPreDrawListener(this);
		// mMemberHeadBack.buildDrawingCache();
		// Bitmap bmp = mMemberHeadBack.getDrawingCache();
		// BlurTools.blur2(ActivityUserModify.this, bmp, mMemberHeadBack);
		return true;

	}

	private void fillInfo(UserExtendsInfo info) {
		String headUrl = info.headerUrl;
		if (TextUtils.isEmpty(headUrl)) {
			headUrl = "";
		} else {
			headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.MODLE,
					ContantsActivity.Image.MODLE);
		}
		mMemberHead.setImageURI(Uri.parse(headUrl));
		// imageLoad.displayImage(headUrl, mMemberHead, headOptions);
//		Glide.with(this).load(headUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(mMemberHead);
		// imageLoad.displayImage(headUrl, mMemberHeadBack, options);
		String nickName = info.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}
		mMemberNickName.setText(nickName);

		boolean isMen = "1".equals(info.sex);
		if (isMen) {
			mMemberSex.setText("男");
		} else {
			mMemberSex.setText("女");
		}
		String signture = info.signature;
		if (TextUtils.isEmpty(signture)) {
			signture = "";
		}
		mMemberSignture.setText(signture);

		String birthDay = info.birthday;
		mMemberBirthDay.setText(birthDay);
		mMemberConstellation.setText(DateUtils.getConstellation(birthDay));

		String cityId = info.cityId;
		String provinceId = info.provinceId;
		String zoneId = info.districtId;

		String city = placeDBManager.getCity(cityId);
		String provice = placeDBManager.getProvice(provinceId);
		String zone = placeDBManager.getZone(zoneId);
		StringBuilder address = new StringBuilder();
		if (provice.contains(city)) {
			address.append(provice);
		} else {
			address.append(provice).append("_").append(city);
		}
		if (!TextUtils.isEmpty(zone)) {
			address.append("_").append(zone);
		}
		mMemberPlace.setText(address.toString());
		initBirthView();
		initAddressView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isFinish = true;
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getModifyUserInfoTag);
		}
		if (getCityInfoTask != null) {
			getCityInfoTask.cancel(true);
			getCityInfoTask = null;
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
		if (placeDBManager != null) {
			placeDBManager = null;
		}
		if (mAddressPickerView != null) {
			mAddressPickerView = null;
		}
		if (mTimePickerView != null) {
			mTimePickerView = null;
		}
		if (menuChoisePhotos != null) {
			menuChoisePhotos = null;
		}
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
