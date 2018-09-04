package com.shizhong.view.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.RecommendDancerAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.db.VideoUploadTaskDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.FileOptionsUtils;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.base.view.UploadVideoSuccessWindow;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.TopicBeanDataPackage;
import com.shizhong.view.ui.bean.UploadVideoTaskBean;
import com.shizhong.view.ui.bean.VideoInfoBean;
import com.shizhong.view.ui.bean.VideoInfoBeanDataPackage;
import com.shizhong.view.ui.video.ActivityMediaVideoRecorder;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivityTopicDetail extends BaseFragmentActivity implements OnClickListener {
	private View mTopicHeadView;
	private LayoutInflater mLayoutInfalter;
	private TextView mTopicContent;
	private TextView mTopicTitleView;

	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;

	private RecommendDancerAdapter mAdapter;
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = 10;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<BaseVideoBean> mDatas = new ArrayList<BaseVideoBean>();
	private String topic_id;
	private String topic_name;
	private int topic_page;
	private String login_token = null;
	private View mUploadView;
	private ProgressBar mUploadProgressBar;
	private TextView mUploadProgressDes;
	private String task_id;
	private UploadVideoSuccessWindow mUploadVideoWindow;
	private String memebr_id;
	private final String share_title = "分享 %s 的失重街舞视频，快来围观。";
	private final String share_title_all = "分享 %s 的失重街舞视频， %s ，快来围观。";
	private View mUploadFailView;
	private TextView mReUploadBtn;
	private TextView mCancelBtn;
	private String getTopicVideosTag = "/video/getTopicVideos";
	private String getTopicDetailsTag = "/topic/getTopicDetails";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_topic_detai);
		mLayoutInfalter = LayoutInflater.from(ActivityTopicDetail.this);
		mTopicHeadView = mLayoutInfalter.inflate(R.layout.head_layout_topic_detail, null);
		findViewById(R.id.left_bt).setOnClickListener(this);
		mTopicContent = (TextView) mTopicHeadView.findViewById(R.id.topic_content);
		mTopicTitleView = (TextView) findViewById(R.id.title_tv);
		mTopicTitleView.setText(topic_name);
		mUploadFailView = findViewById(R.id.video_upload_fail_layout);
		mReUploadBtn = (TextView) findViewById(R.id.reupload_btn);
		mReUploadBtn.setOnClickListener(this);
		mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
		mUploadFailView.setVisibility(View.GONE);
		mCancelBtn.setOnClickListener(this);
		mUploadView = findViewById(R.id.prgress_layout);
		mUploadView.setVisibility(View.GONE);
		mUploadProgressBar = (ProgressBar) findViewById(R.id.upload_progress_bar);
		mUploadProgressDes = (TextView) findViewById(R.id.upload_des);
		findViewById(R.id.tab_camera).setOnClickListener(this);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		listView.addHeaderView(mTopicHeadView);
		mAdapter = new RecommendDancerAdapter(ActivityTopicDetail.this, mDatas);
		listView.setAdapter(mAdapter);
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

		requsetTopicDetail();

		if (mDatas.size() <= 0) {
			loadAUTO();
		}
	}

	private void showUploadView(String uploadDes) {
		mUploadProgressBar.setMax(102);
		mUploadProgressDes.setText(uploadDes);
		Animation mAppShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mUploadView.startAnimation(mAppShowAction);
		mUploadView.setVisibility(View.VISIBLE);
	}

	private void hideUploadView() {
		if (mUploadView.getVisibility() == View.VISIBLE) {
			mUploadProgressBar.setMax(0);
			mUploadProgressDes.setText("");
			Animation mAppShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
					0.0f);
			mUploadView.startAnimation(mAppShowAction);
			mUploadView.setVisibility(View.GONE);
		}
	}

	private void requsetTopicDetail() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("topicId", topic_id);
		LogUtils.e("获取话题详情", "-------");
		BaseHttpNetMananger.getInstance(ActivityTopicDetail.this).postJSON(ActivityTopicDetail.this, getTopicDetailsTag,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						TopicBeanDataPackage dataPackage = GsonUtils.json2Bean(req, TopicBeanDataPackage.class);
						int code = dataPackage.code;
						if (code != 100001) {
							ToastUtils.showShort(ActivityTopicDetail.this, "获取视频详情失败");
							return;
						}

						TopicBean bean = dataPackage.data;
						if (bean != null) {
							String content = bean.description;
							mTopicContent.setText(content);
							String title = bean.topicName;
							mTopicTitleView.setText("#" + title + "#");
						}

					}

					@Override
					public void requestFail() {
						loadNoMore();

					}

					@Override
					public void requestNetExeption() {
						loadNoMore();
					}
				}, false);

	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	/**
	 * 获取舞社视频详情
	 */
	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("nowPage", nowPage + "");
		params.put("topicId", topic_id);
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取舞社视频详情", "-------");
		BaseHttpNetMananger.getInstance(ActivityTopicDetail.this).postJSON(ActivityTopicDetail.this, getTopicVideosTag,
				params, new IRequestResult() {

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
								ToastUtils.showErrorToast(ActivityTopicDetail.this, code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						BaseVideoList data = GsonUtils.json2Bean(req, BaseVideoList.class);
						if (data == null) {
							loadNoMore();
							return;
						}
						List<BaseVideoBean> list = data.data;
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
						mAdapter.notifyDataSetChanged();
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
						loadNoMore();

					}

					@Override
					public void requestNetExeption() {
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
			}
		}, 1000);
	}

	@Override
	protected void initBundle() {
		topic_id = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_ID);
		topic_name = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_NAME);
		topic_page = getIntent().getIntExtra(ContantsActivity.Topic.TOPIC_PAGE, 1);
		login_token = PrefUtils.getString(ActivityTopicDetail.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		memebr_id = PrefUtils.getString(ActivityTopicDetail.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			if (VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id).isHasVideoUpload()) {
				showAlterDialog();
			} else {
				finish();
			}
			break;
		case R.id.tab_camera:
			if (VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id).isHasVideoUpload()) {
				ToastUtils.showShort(ActivityTopicDetail.this, "当前有视频正在上传请稍后。。。");
				return;
			}
			mIntent.setClass(ActivityTopicDetail.this, ActivityMediaVideoRecorder.class);
			if (!TextUtils.isEmpty(topic_name)) {
				mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name.replaceAll("#", ""));
				mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
			}
			mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
			startActivityForResult(mIntent, ContantsActivity.Action.ACTION_PUSH_VIDEO);
			break;
		case R.id.reupload_btn:
			mUploadFailView.setVisibility(View.GONE);
			reUploadVideo();
			break;

		case R.id.cancel_btn:
			mUploadFailView.setVisibility(View.GONE);
			delectVideoTask();
			break;
		}
	}

	private void reUploadVideo() {
		String uploadDes = "正在上传...";
		if (mUploadVideoBean != null) {
			switch (mUploadVideoBean.code_task) {
			case 0:
				uploadDes = "开始上传..";
				break;
			case -1:
				uploadDes = "重新上传..";
				break;
			case 2:
				uploadDes = "正在上传..";
				break;
			default:
				break;
			}
			String videoPath = mUploadVideoBean.videoPath;
			File videFile = null;
			if (!TextUtils.isEmpty(videoPath) && (videFile = new File(videoPath)).exists()) {
				showUploadView(uploadDes);
				getVideoToken(login_token, BaseQiNiuRequest.MODEL_ACTION_VIDEO, videFile);
			}

		}
	}

	private void delectVideoTask() {
		if (task_id != null) {
			FileOptionsUtils.removeVideo(ActivityTopicDetail.this, memebr_id, task_id);
			VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id).delectedTask(task_id);
		}
	}

	private Dialog mBackAlertDialog;

	private void showAlterDialog() {
		if (mBackAlertDialog == null) {
			mBackAlertDialog = DialogUtils.confirmDialog(ActivityTopicDetail.this, "是否确定取消上传", "确定取消", "继续上传",
					new ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							if (memebr_id != null && task_id != null) {
								FileOptionsUtils.removeVideo(ActivityTopicDetail.this, memebr_id, task_id);
								VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
										.delectedTask(task_id);
							}
							finish();
						}

						@Override
						public void onCancleClick(Dialog dialog) {
						}
					});
		}

		if (!mBackAlertDialog.isShowing()) {
			mBackAlertDialog.show();
		}
	}

	private UploadVideoTaskBean mUploadVideoBean;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter != null) {
			mAdapter.onActivityResult(requestCode, resultCode, data);
		}

		if (requestCode == ContantsActivity.Action.ACTION_PUSH_VIDEO) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					task_id = data.getStringExtra(ContantsActivity.Video.VIDEO_TASK_ID);
					mUploadVideoBean = VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
							.findTask(task_id);

					String videoPath = mUploadVideoBean.videoPath;
					File videFile = null;

					if (!TextUtils.isEmpty(videoPath) && (videFile = new File(videoPath)).exists()) {
						getVideoToken(login_token, BaseQiNiuRequest.MODEL_ACTION_VIDEO, videFile);
						VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
								.updateVideoUploadTaskState(task_id, 2);
					}
					if (mUploadVideoBean != null && mUploadVideoBean.code_task != 1) {
						showUploadView("正在上传视频...");
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
			}
		}
	}

	public void getVideoToken(String loginToken, String type, final File videFile) {
		String baseURL = "/media/getUpToken";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", loginToken);
		LogUtils.e("获取视频token", "-------");
		BaseHttpNetMananger.getInstance(ActivityTopicDetail.this).postJSON(ActivityTopicDetail.this, baseURL, params,
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
								if (mUploadProgressBar != null) {
									mUploadProgressBar.setProgress(1);
								}
								// 获取uptoken之后上传到七牛服务器
								uploadVideo(up_token, videFile);

								break;
							case 900001:
								msg = root.getString("msg");
								break;
							default:
								ToastUtils.showErrorToast(ActivityTopicDetail.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.net_error));

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.net_error));
					}
				}, false);
	}

	private UploadManager mUploadVideoManager = null;

	public void uploadVideo(String up_token, File videoFile) {
		if (this.mUploadVideoManager == null) {
			this.mUploadVideoManager = new UploadManager();
		}

		UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
			@Override
			public void progress(String s, final double percent) {
				AsyncRun.run(new Runnable() {
					@Override
					public void run() {
						if (mUploadProgressBar != null) {
							mUploadProgressBar.setProgress((int) ((percent * 100) + 1));
						}
					}
				});

			}
		}, null);
		this.mUploadVideoManager.put(videoFile, BaseQiNiuRequest.getVideoFileKey(), up_token,
				new UpCompletionHandler() {
					@Override
					public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {

						if (responseInfo.isOK()) {
							try {
								String fileKey = jsonObject.getString("key");
								String videoFileName = fileKey;
								if (!TextUtils.isEmpty(task_id)) {
									VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
											.updateVideoUploadState(task_id, videoFileName, 1);
									uploadVideo(task_id);
								}

							} catch (JSONException e) {
								e.printStackTrace();
								ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.error_jsonpare));
							}

						} else {
							ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.error_member_head));
						}

					}
				}, uploadOptions);
	}

	private void uploadVideo(final String task_id) {
		UploadVideoTaskBean bean = VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
				.findTask(task_id);
		String rootUrl = "/video/add";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoUrl", bean.videoFileName);
		params.put("coverUrl", bean.coverFileName);
		params.put("categoryId", bean.categroyId);
		String description = bean.description;
		if (!TextUtils.isEmpty(description)) {
			params.put("description", description);
		}
		String topicId = bean.topicId;
		if (!TextUtils.isEmpty(topicId)) {
			params.put("topicId", topicId);
		}
		long videoLength = bean.videoLength;
		if (videoLength > 0) {
			params.put("videoLength", videoLength + "");
		}
		LogUtils.e("上传视频", "-------");
		BaseHttpNetMananger.getInstance(ActivityTopicDetail.this).postJSON(ActivityTopicDetail.this, rootUrl, params,
				new IRequestResult() {

					@SuppressWarnings("static-access")
					@Override
					public void requestSuccess(String req) {
						int code = 0;
						String msg = "";
						try {
							JSONObject rootObj = new JSONObject(req);
							code = rootObj.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = rootObj.getString("msg");
								}
								ToastUtils.showErrorToast(ActivityTopicDetail.this, code, msg, true);
								mUploadFailView.setVisibility(View.VISIBLE);
								hideUploadView();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						VideoInfoBeanDataPackage dataPackage = GsonUtils.json2Bean(req, VideoInfoBeanDataPackage.class);
						if (code == 100001) {
							VideoInfoBean data = dataPackage.data;
							if (mUploadProgressBar != null && mUploadProgressDes != null) {
								mUploadProgressBar.setProgress(102);
								mUploadProgressDes.setText("上传成功");
							}
							if (data != null) {
								String video_id = data.videoId;
								FileOptionsUtils.removeVideo(ActivityTopicDetail.this, memebr_id, task_id);
								VideoUploadTaskDBManager.getInstance(ActivityTopicDetail.this, memebr_id)
										.updateVideoUploadTaskState(task_id, 1, video_id);
								String des = data.description;
								String nickName = data.memberInfo.nickname;
								String title = null;
								if (TextUtils.isEmpty(des)) {
									title = String.format(share_title, nickName);
								} else {
									title = new String().format(share_title_all, nickName, des);
								}

								Map<String, String> event = new HashMap<String, String>();
								event.put("video_id", video_id);
								event.put("type", "视频上传数量");
								event.put("time",
										com.hyphenate.easeui.utils.DateUtils.format9(System.currentTimeMillis()));
								MobclickAgent.onEvent(ActivityTopicDetail.this, "video_add_ID", event);
								ShareContentBean sharebean = new ShareContentBean();
								sharebean.shareVideo = data.videoUrl;
								sharebean.shareImage = data.coverUrl;
								sharebean.shareContent = title;
								sharebean.shareUrl = data.shareUrl;
								showUploadSuccessWindow(sharebean);
							}
						}
					}

					@Override
					public void requestNetExeption() {
						mUploadView.setVisibility(View.GONE);
						mUploadFailView.setVisibility(View.VISIBLE);
						ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.net_error));
					}

					@Override
					public void requestFail() {
						mUploadView.setVisibility(View.GONE);
						mUploadFailView.setVisibility(View.VISIBLE);
						ToastUtils.showShort(ActivityTopicDetail.this, getString(R.string.net_error));

					}
				}, false);
	}

	/**
	 * 上传成功
	 * 
	 * @param
	 */
	private void showUploadSuccessWindow(ShareContentBean sharebean) {
		hideUploadView();
		if (mUploadVideoWindow == null) {
			mUploadVideoWindow = new UploadVideoSuccessWindow(ActivityTopicDetail.this);
		}
		mUploadVideoWindow.setShareBean(sharebean);
		if (!mUploadVideoWindow.isShowing()) {
			mUploadVideoWindow.show(mPullToRefreshListView);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getTopicDetailsTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getTopicVideosTag);
		}

		if (mBackAlertDialog != null) {
			mBackAlertDialog = null;
		}
		if (mUploadVideoWindow != null) {
			mUploadVideoWindow = null;
		}
	}

}
