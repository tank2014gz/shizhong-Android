package com.shizhong.view.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.adapter.ActivitySearchFriendsAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.UserExtendsListDataPakage;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ActivityAddFriends extends BaseFragmentActivity implements OnClickListener {

	private ClearEditText mEditText;
	private TextView mCancleBtn;
	private TextView mSearchBtn;
	private View mContentNullView;
	private TextView mNullTextView;
	private String loginToken; // 用户token
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;

	private BaseAdapter mFirendsAdapter;
	private ArrayList<UserExtendsInfo> mDatas = new ArrayList<UserExtendsInfo>();
	private String mSearchText = "搜索 \"%s\"";
	private String searchValue = "";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_add_friends);
		mContentNullView = findViewById(R.id.null_view);
		mNullTextView = (TextView) findViewById(R.id.tv_null_text);
		mNullTextView.setText("很抱歉，暂时没有找到相关的用户");
		mEditText = (ClearEditText) findViewById(R.id.search_edit);
		mCancleBtn = (TextView) findViewById(R.id.cancle_btn);
		mCancleBtn.setOnClickListener(this);
		mSearchBtn = (TextView) findViewById(R.id.search_btn);
		mSearchBtn.setOnClickListener(this);
		mSearchBtn.setVisibility(View.GONE);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		mFirendsAdapter = new ActivitySearchFriendsAdapter(ActivityAddFriends.this, mDatas);
		listView.setAdapter(mFirendsAdapter);
		mEditText.addTextChangedListener(mTextWatcher);
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					InputMethodUtils.hide(ActivityAddFriends.this, mEditText);
					mSearchBtn.setVisibility(View.GONE);
					mDatas.clear();
					mFirendsAdapter.notifyDataSetChanged();
					loadAUTO();
					return true;
				}
				return false;
			}
		});
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				isLoadMore = false;
				nowPage = 1;
				requestData(pullToRefreshLayout, isLoadMore);
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				isLoadMore = true;

				if (isHasMore) {
					requestData(pullToRefreshLayout, isLoadMore);
				} else {
					loadNoMore();
				}

			}
		});

	}

	@Override
	protected void initBundle() {
		loginToken = PrefUtils.getString(ActivityAddFriends.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn:
			InputMethodUtils.hide(ActivityAddFriends.this, mEditText);
			mSearchBtn.setVisibility(View.GONE);
			mDatas.clear();
			mFirendsAdapter.notifyDataSetChanged();
			loadAUTO();
			break;
		case R.id.cancle_btn:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(0, R.anim.push_bottom_out);
	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		String rootURL = "/search/getList";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", loginToken);
		params.put("text", searchValue);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("搜索好友", "-------");
		BaseHttpNetMananger.getInstance(ActivityAddFriends.this).postJSON(ActivityAddFriends.this, rootURL, params,
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						int code = 0;
						String msg = "";

						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(ActivityAddFriends.this, code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						UserExtendsListDataPakage data = GsonUtils.json2Bean(req, UserExtendsListDataPakage.class);
						if (data == null) {
							loadNoMore();
							return;
						}
						List<UserExtendsInfo> list = data.data;
						if (list == null || list.size() <= 0) {
							loadNoMore();
							return;
						}

						if (list.size() < recordNum) {
							isHasMore = false;
						} else {
							isHasMore = true;
						}

						if (!isLoadMore) {
							mDatas.clear();
						}
						nowPage++;
						mDatas.addAll(list);
						mFirendsAdapter.notifyDataSetChanged();
						if (mContentNullView.getVisibility() == View.VISIBLE) {
							mContentNullView.setVisibility(View.GONE);
						}
						mMainHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (isLoadMore) {
									pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								} else {
									pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
								}

							}
						}, 1000);

					}

					@Override
					public void requestFail() {
						LogUtils.e("搜索好友", "-网络异常----");
						loadNoMore();

					}

					@Override
					public void requestNetExeption() {
						LogUtils.e("搜索好友", "----网络异常---");
						loadNoMore();
					}
				}, false);
	}

	private void loadNoMore() {
		mMainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isLoadMore) {
					mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.FAIL);
				} else {
					mPullToRefreshListView.refreshFinish(PullToRefreshLayout.FAIL);
				}
				if (mDatas.size() <= 0) {
					mContentNullView.setVisibility(View.VISIBLE);
				} else {
					mContentNullView.setVisibility(View.GONE);
				}
			}
		}, 1000);

	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			searchValue = mEditText.getText().toString();
			if (searchValue.length() <= 0) {
				mSearchBtn.setText("");
				mSearchBtn.setVisibility(View.GONE);
			} else {
				mSearchBtn.setText(String.format(mSearchText, searchValue));
				mSearchBtn.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};
}
