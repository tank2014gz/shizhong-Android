package com.shizhong.view.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shizhong.view.ui.adapter.DancesClassAdapter;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.MyGridView;
import com.shizhong.view.ui.bean.DanceClass;
import com.shizhong.view.ui.bean.DanceList;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuliyan on 15/12/26.
 */
public class DanceClassChoiseActivity extends BaseUmengActivity implements View.OnClickListener {
	private String login_token;
	private MyGridView myGridView;
	private TextView mFinishChoise;
	@SuppressWarnings("rawtypes")
	private BaseSZAdapter mAdapter;
	private ArrayList<DanceClass> mDatas = new ArrayList<DanceClass>();
	private ArrayList<String> dancesNames = new ArrayList<String>();// 已经选择的舞种

	private String getCategoryTag = "/category/getAll";
	private String getCommitCategory = "/member/choosecategory";

	private void getDanceList() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		LogUtils.e("获取舞蹈种类列表", "-------");
		showLoadingDialog();
		BaseHttpNetMananger.getInstance(DanceClassChoiseActivity.this).postJSON(DanceClassChoiseActivity.this,
				getCategoryTag, params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						String msg = "";

						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(DanceClassChoiseActivity.this, code, msg, true);
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						DanceList list = GsonUtils.json2Bean(req, DanceList.class);
						mDatas.addAll(list.data);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(DanceClassChoiseActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(DanceClassChoiseActivity.this, getString(R.string.net_conected_error));
					}
				}, true);

	}

	@Override
	protected void initView() {
		setContentView(R.layout.layout_dance_class);
		myGridView = (MyGridView) findViewById(R.id.dances_grid);
		mAdapter = new DancesClassAdapter(DanceClassChoiseActivity.this, mDatas, this);
		myGridView.setAdapter(mAdapter);
		mFinishChoise = (TextView) findViewById(R.id.choise_finish);
		mFinishChoise.setOnClickListener(this);
		getDanceList();
	}

	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(DanceClassChoiseActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		if (TextUtils.isEmpty(login_token)) {
			ToastUtils.showShort(DanceClassChoiseActivity.this, getString(R.string.token_null));
		}
	}

	@Override
	protected void initData() {
		// getDanceClassAll();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.choise_finish:
			StringBuilder categoryIds = new StringBuilder();
			int len = dancesNames.size();
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					categoryIds.append(dancesNames.get(i));
				} else {
					categoryIds.append(",").append(dancesNames.get(i));
				}
			}
			if (TextUtils.isEmpty(categoryIds.toString())) {
				ToastUtils.showShort(DanceClassChoiseActivity.this, "至少选择一个舞种");
				return;
			}
			setDanceCategory(categoryIds.toString());

			break;
		case R.id.selected:
		case R.id.dance_image_id:
			DanceClass d = (DanceClass) view.getTag(R.string.app_name);
			int pos = d.position;
			if (dancesNames.size() >= 0 && dancesNames.size() < 3) {
				d.isSelected = (!d.isSelected);
				mDatas.set(pos, d);
				mAdapter.notifyDataSetChanged();
				String catergramId = d.categoryId;

				if (dancesNames.contains(catergramId)) {
					dancesNames.remove(catergramId);
				} else {
					dancesNames.add(catergramId);
				}
			} else {
				if (d.isSelected) {
					d.isSelected = (!d.isSelected);
					mDatas.set(pos, d);
					mAdapter.notifyDataSetChanged();
					String catergramId = d.categoryId;
					dancesNames.remove(catergramId);
				}
				ToastUtils.showShort(DanceClassChoiseActivity.this, "最多选择3个舞种");
			}
			break;
		}
	}

	// private void getDanceClassAll() {
	//
	// String baseURL = "/news/getList";
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("token", login_token);
	// showLoadingDialog();
	// LogUtils.e("获取舞蹈种类列表", "-------");
	// BaseHttpNetMananger.getInstance(DanceClassChoiseActivity.this).postJSON(DanceClassChoiseActivity.this,
	// baseURL,
	// params, new IRequestResult() {
	// @Override
	// public void requestSuccess(String req) {
	// dismissLoadingDialog();
	// try {
	// JSONObject json = new JSONObject(req);
	// int code = json.getInt("code");
	// switch (code) {
	// case 100001:
	// mIntent.setClass(DanceClassChoiseActivity.this, MainActivity.class);
	// startActivityForResult(mIntent, -1);
	// finish();
	// break;
	//
	// default:
	// break;
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void requestFail() {
	// dismissLoadingDialog();
	// ToastUtils.showShort(DanceClassChoiseActivity.this,
	// getString(R.string.net_error));
	//
	// }
	//
	// @Override
	// public void requestNetExeption() {
	// dismissLoadingDialog();
	// ToastUtils.showShort(DanceClassChoiseActivity.this,
	// getString(R.string.net_conected_error));
	// }
	// }, true);
	// }

	private void setDanceCategory(String category) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("categoryId", category);
		LogUtils.e("选择舞蹈种类提交", "-------");
		showLoadingDialog();
		BaseHttpNetMananger.getInstance(DanceClassChoiseActivity.this).postJSON(DanceClassChoiseActivity.this,
				getCommitCategory, params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						try {
							JSONObject rootJson = new JSONObject(req);
							int code = rootJson.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								setResult(ContantsActivity.Action.RESULT_ACTION_REG_SUCCESS);
								onBackPressed();
								break;
							case 900001:
								msg = rootJson.getString("msg");
							default:
								ToastUtils.showErrorToast(DanceClassChoiseActivity.this, code, msg, true);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(DanceClassChoiseActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(DanceClassChoiseActivity.this, getString(R.string.net_conected_error));
					}
				}, true);
	}

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getCategoryTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getCommitCategory);
		}
		if (mDatas != null) {
			mDatas.clear();
			mDatas = null;
		}
		if (dancesNames != null) {
			dancesNames.clear();
			dancesNames = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		System.gc();
	}
}
