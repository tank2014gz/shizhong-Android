package com.shizhong.view.ui.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.TagesAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.db.VideoUploadTaskDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.DanceClassWindow;
import com.shizhong.view.ui.base.view.DanceClassWindow.OnDanceChoiseListener;
import com.shizhong.view.ui.base.view.tag.TagAdapter;
import com.shizhong.view.ui.base.view.tag.TagFlowLayout;
import com.shizhong.view.ui.base.view.tag.TagFlowLayout.OnSelectListener;
import com.shizhong.view.ui.bean.DanceClass;
import com.shizhong.view.ui.bean.DanceList;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.TopicDataPackage;
import com.shizhong.view.ui.bean.UploadVideoTaskBean;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityShareVideo extends BaseFragmentActivity implements OnClickListener {

	private TagFlowLayout mFlowLayout;
	private TagAdapter<TopicBean> mAdapter;
	private String login_token;

	private UploadVideoTaskBean uploadTask;
	private String taskId = null;

	private List<TopicBean> mDatas = new ArrayList<TopicBean>();
	/**
	 * 视频信息
	 */
	private String mVideoPath;
	private String mCoverPath;
	private int mVideoDuring;
	private ImageView mVideoCoverBgImage;
	private ImageView mVideoCoverImage;
	private EditText mVideoDesEdit;
	private final int recordNum = 10;
	private int nowPage = 1;
	private boolean isHasMore;
	private TextView mChangeNext;
	private String mTopicName;
	private String mTopicId;
	private TextView mCommiteView;
	private String topic_name;
	private int topic_page;
	private  String topic_id;

	private String memebr_id;
	private DanceClassWindow mAddDanceClassWindow;
	private String choseDances;
	private ArrayList<DanceClass> titles = new ArrayList<DanceClass>();

	private String getImageTokenTag = "/media/getUpToken";
	private String getTopicsTag = "/topic/getTopics";
	private String getCategoryTag = "/category/getAll";

	private void showDanceWindow(View view) {
		if (mAddDanceClassWindow == null && titles != null && titles.size() > 0) {
			mAddDanceClassWindow = new DanceClassWindow(ActivityShareVideo.this, titles, "选择舞种后发布");
			mAddDanceClassWindow.setOnDanceChoiseListener(new OnDanceChoiseListener() {

				@Override
				public void choiseListener(int position) {
					choseDances = titles.get(position).categoryId;
					showCommiteDialog();

				}
			});
		}

		if (!mAddDanceClassWindow.isShowing()) {
			mAddDanceClassWindow.show(view);
		} else {
			mAddDanceClassWindow.dismiss();
		}
	}

	private Dialog mCommitDialog;

	private void showCommiteDialog() {

		mCommitDialog = DialogUtils.confirmDialog(ActivityShareVideo.this, "确定选择 " + choseDances + " 进行发布", "确定发布",
				"取消重选", new ConfirmDialog() {

					@Override
					public void onOKClick(Dialog dialog) {
						File mCoverFile = null;
						if (TextUtils.isEmpty(mCoverPath) || !(mCoverFile = new File(mCoverPath)).exists()) {
							ToastUtils.showShort(ActivityShareVideo.this, "找不到封面图片文件");
							return;
						}
						if (TextUtils.isEmpty(mVideoPath) || !(new File(mVideoPath)).exists()) {
							ToastUtils.showShort(ActivityShareVideo.this, "找不到视频文件");
							return;
						}
						if (TextUtils.isEmpty(choseDances)) {
							ToastUtils.showShort(ActivityShareVideo.this, "选择舞种失败");
							return;
						}

						String des = mVideoDesEdit.getText().toString();
						if (uploadTask == null) {
							uploadTask = new UploadVideoTaskBean();
							taskId = UUID.randomUUID().toString();
							uploadTask.coverPath = mCoverPath;
							uploadTask.videoPath = mVideoPath;
							uploadTask.taskId = taskId;
							uploadTask.description = des;
							uploadTask.categroyId = choseDances;
							uploadTask.topicId = mTopicId;
							uploadTask.videoLength = mVideoDuring / 1000;
							VideoUploadTaskDBManager.getInstance(ActivityShareVideo.this, memebr_id)
									.insertTask(uploadTask);
						} else {
							if (taskId == null) {
								taskId = uploadTask.taskId;
							}
						}
						getImageUploadToken(login_token, BaseQiNiuRequest.MODEL_ACTION_VIDEO_COVER, mCoverFile);
					}

					@Override
					public void onCancleClick(Dialog dialog) {
						showDanceWindow(mCommiteView);
					}
				});
		mCommitDialog.show();

	}

	/**
	 * 获取titles 标题
	 */
	private void getTitles() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		LogUtils.e("获取所有舞蹈种类列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityShareVideo.this).postJSON(ActivityShareVideo.this, getCategoryTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						DanceList list = GsonUtils.json2Bean(req, DanceList.class);
						int code = list.code;
						switch (code) {
						case 100001:
							titles.clear();
							titles.addAll(list.data);
							break;
						}
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.net_conected_error));
					}
				}, true);

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_share_video_layout);
		((TextView) findViewById(R.id.title_tv)).setText("分享");
		findViewById(R.id.left_bt).setOnClickListener(this);

		mVideoCoverBgImage = (ImageView) findViewById(R.id.video_cover_bg);
		mVideoCoverImage = (ImageView) findViewById(R.id.video_cover);
		mVideoCoverImage.setOnClickListener(this);
		mChangeNext = (TextView) findViewById(R.id.change_tags);
		mChangeNext.setOnClickListener(this);
		mVideoDesEdit = (EditText) findViewById(R.id.video_des);
		mCommiteView = (TextView) findViewById(R.id.commite);
		mCommiteView.setOnClickListener(this);
		mFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
		mAdapter = new TagesAdapter(mDatas, ActivityShareVideo.this);
		mFlowLayout.setAdapter(mAdapter);

		mFlowLayout.setOnSelectListener(new OnSelectListener() {

			@Override
			public void onSelected(Set<Integer> selectPosSet) {
				// StringBuffer info = new StringBuffer();
				for (Integer position : selectPosSet) {
					// info.append(mDatas.get(position).getTopicName() + ",");
					mTopicName = mDatas.get(position).topicName;
					mTopicId=mDatas.get(position).topicId;
				}

			}
		});
	}

	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(ActivityShareVideo.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		memebr_id = PrefUtils.getString(ActivityShareVideo.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		if (getIntent() != null) {
			mVideoPath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_PATH);
			mVideoDuring = getIntent().getIntExtra(ContantsActivity.Video.VIDEO_DURING, 0);
			mCoverPath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_COVER_PATH);
		}
		topic_name = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_NAME);
		topic_page = getIntent().getIntExtra(ContantsActivity.Topic.TOPIC_PAGE, 1);
		topic_id=getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_ID);
		nowPage = topic_page;
	}

	/**
	 * 加载本地图片 http://bbs.3gstdy.com
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void initData() {
		if (mCoverPath != null && !TextUtils.isEmpty(mCoverPath)) {
			mVideoCoverImage.setImageBitmap(getLoacalBitmap(mCoverPath));
			mVideoCoverBgImage.setImageBitmap(getLoacalBitmap(mCoverPath));
		}
		getTopicList();
		getTitles();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.video_cover:
			mIntent.setClass(ActivityShareVideo.this, ActivityVideoCoverChange.class);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, mVideoPath);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_DURING, mVideoDuring);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_COVER_PATH, mCoverPath);
			startActivityForResult(mIntent, ContantsActivity.Action.ACTION_CHANGE_VIDEO_COVER);
			break;
		case R.id.change_tags:
			if (isHasMore) {
				nowPage++;
			} else {
				nowPage = 1;
			}
			getTopicList();
			break;
		case R.id.commite:
			showDanceWindow(v);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		super.onActivityResult(req, res, data);
		switch (req) {
		case ContantsActivity.Action.ACTION_CHANGE_VIDEO_COVER:
			if (res == Activity.RESULT_OK) {
				if (mCoverPath != null && !TextUtils.isEmpty(mCoverPath)) {
					mVideoCoverImage.setImageBitmap(getLoacalBitmap(mCoverPath));
					mVideoCoverBgImage.setImageBitmap(getLoacalBitmap(mCoverPath));
				}
			}
			break;

		default:
			break;
		}

	}

	private void getTopicList() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取标题列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityShareVideo.this).postJSON(ActivityShareVideo.this, getTopicsTag, params,
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
								ToastUtils.showErrorToast(ActivityShareVideo.this, code, msg, true);
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							return;
						}

						TopicDataPackage dataPackage = GsonUtils.json2Bean(req, TopicDataPackage.class);

						List<TopicBean> list = dataPackage.data;

						if (list == null || list.size() <= 0) {
							return;
						}

						if (list.size() < recordNum) {
							isHasMore = false;
						} else {
							isHasMore = true;
						}
						nowPage++;
						mDatas.clear();
						mDatas.addAll(list);
						int size = mDatas.size();
						mAdapter.notifyDataChanged();
						for (int i = 0; i < size; i++) {
							if (!TextUtils.isEmpty(topic_name) && mDatas.get(i).topicName.equals(topic_name)) {
								mAdapter.setSelectedList(i);
								mTopicName = topic_name;
								mTopicId=topic_id;
							}
						}
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityShareVideo.this,
								ActivityShareVideo.this.getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityShareVideo.this,
								ActivityShareVideo.this.getString(R.string.net_conected_error));
					}
				}, false);

	}

	private void getImageUploadToken(String login_token, String type, final File file) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", login_token);
		LogUtils.e("获取上传图片token", "-------");
		BaseHttpNetMananger.getInstance(ActivityShareVideo.this).postJSON(ActivityShareVideo.this, getImageTokenTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								String up_token = root.getString("data");
								// 获取uptoken之后上传到七牛服务器
								uploadHeadImage(up_token, file);

								break;
							case 900001:
								msg = root.getString("msg");
							default:
								dismissLoadingDialog();
								ToastUtils.showErrorToast(ActivityShareVideo.this, code, msg, true);
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							dismissLoadingDialog();
							ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.net_error));
						dismissLoadingDialog();

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.net_error));
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
						String coverUrl = fileKey;
						if (uploadTask != null && taskId != null) {
							uploadTask.coverFileName = coverUrl;
							VideoUploadTaskDBManager.getInstance(ActivityShareVideo.this, memebr_id)
									.updateCoverUploadState(taskId, coverUrl, 1);

							Intent intent = getIntent();
							if (intent == null) {
								intent = new Intent();
							}
							intent.putExtra(ContantsActivity.Video.VIDEO_TASK_ID, taskId);
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						ToastUtils.showShort(ActivityShareVideo.this, getString(R.string.error_jsonpare));
					}

				} else {
					VideoUploadTaskDBManager.getInstance(ActivityShareVideo.this, memebr_id)
							.updateCoverUploadState(taskId, -1);
					ToastUtils.showShort(ActivityShareVideo.this, "上传封面图片失败");
				}

			}
		}, null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getCategoryTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getTopicsTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getImageTokenTag);
		}

		if (titles != null) {
			titles.clear();
			titles = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (uploadTask != null) {
			uploadTask = null;
		}
		if (mUploadHeadManager != null) {
			mUploadHeadManager = null;
		}
		if (mAddDanceClassWindow != null) {
			mAddDanceClassWindow = null;
		}
		if (mFlowLayout != null) {
			mFlowLayout.removeAllViews();
			mFlowLayout = null;
		}
		if (mCommitDialog != null) {
			mCommitDialog = null;
		}

		System.gc();
	}
}
