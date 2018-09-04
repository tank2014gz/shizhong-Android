package com.shizhong.view.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.db.VideoUploadTaskDBManager;
import com.shizhong.view.ui.base.net.BaseQiNiuRequest;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.DataCleanManager;
import com.shizhong.view.ui.base.utils.FileOptionsUtils;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.UploadVideoSuccessWindow;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.UploadVideoTaskBean;
import com.shizhong.view.ui.bean.VideoInfoBean;
import com.shizhong.view.ui.bean.VideoInfoBeanDataPackage;
import com.umeng.analytics.MobclickAgent;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;

public class Fragment_Home extends BaseFragment implements  OnClickListener {

	private static final String TAG = Fragment_Home.class.getSimpleName();
	private Fragment_HomeDynamic mDynamicFragment;
	private Fragment_HomeRecommend mRecodeFragmnet;
	private Fragment [] titleFragments;
//	private TextView mTopRecodeButton;
//	private TextView mTopDynamicButton;
	private TextView [] titles;
	private ImageView mTopTagView;
	private String login_token;
	private View mUploadView;
	private ProgressBar mUploadProgressBar;
	private TextView mUploadProgressDes;
	private View mUploadFailView;
	private TextView mReUploadBtn;
	private TextView mCancelBtn;
	private String task_id;
	private String memebr_id;
	private UploadVideoSuccessWindow mUploadVideoWindow;
	private final String share_title = "分享 %s 的失重街舞视频，快来围观。";
	private final String share_title_all = "分享 %s 的失重街舞视频， %s ，快来围观。";
	private String getAddVideoTag = "/video/add";
	private String getVideoTokenTag = "/media/getUpToken";
	private int index;
	private int currentTabIndex;

	private ACache mDynamicCache;

	@Override
	public void initBundle() {
		login_token = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		memebr_id = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		mDynamicCache=ACache.get(getActivity());
	}

	@Override
	public int initRootView() {
		return R.layout.fragment_home;
	}

	@Override
	public void initView() {
		mRecodeFragmnet = new Fragment_HomeRecommend();
		mDynamicFragment = new Fragment_HomeDynamic();
		titleFragments=new Fragment[]{mRecodeFragmnet,mDynamicFragment};
		getFragmentManager().beginTransaction().add(R.id.home_center_layout, mRecodeFragmnet)
				.add(R.id.home_center_layout, mDynamicFragment).hide(mDynamicFragment).show(mRecodeFragmnet).commit();

		titles=new TextView[2];
		titles[0]=(TextView) findViewById(R.id.recommended_title);
		titles[0].setOnClickListener(this);
		titles[1]=(TextView)findViewById(R.id.dynaamic_title);
		titles[1].setOnClickListener(this);
		titles[0].setSelected(true);

		mTopTagView=(ImageView) findViewById(R.id.message_tag);
		mTopTagView.setVisibility(View.INVISIBLE);
		mUploadView = findViewById(R.id.prgress_layout);
		mUploadView.setVisibility(View.GONE);
		mUploadProgressBar = (ProgressBar) findViewById(R.id.upload_progress_bar);
		mUploadProgressDes = (TextView) findViewById(R.id.upload_des);
		mUploadFailView = findViewById(R.id.video_upload_fail_layout);
		mReUploadBtn = (TextView) findViewById(R.id.reupload_btn);
		mReUploadBtn.setOnClickListener(onClickListener);
		mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
		mUploadFailView.setVisibility(View.GONE);
		mCancelBtn.setOnClickListener(onClickListener);
//		isUpdateUI = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.e(TAG, "onResume()");

	}

	@Override
	public void initData() {

		mUploadVideoBean = VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id).findTaskUnSuccessUpload();
		if (mUploadVideoBean != null) {
			task_id = mUploadVideoBean.taskId;
			showAlterDialogReUpload();
		} else {
			new Thread() {
				public void run() {
					if (FileUtils.checkFile(CameraManager.getVideoCachePath())) {
						DataCleanManager.deleteFolderFile(CameraManager.getVideoCachePath(), true);
					}
				};
			}.start();
		}
//		isUpdateUI = false;
		String cache=mDynamicCache.getAsString(Fragment_HomeDynamic.mDynamicCache);
		if(TextUtils.isEmpty(cache)){
			mDynamicFragment.setHasNewDate(true);
			mTopTagView.setVisibility(View.VISIBLE);
		}else{
			BaseVideoList data = GsonUtils.json2Bean(cache, BaseVideoList.class);
			List<BaseVideoBean> list = data.data;
			String time=list.get(0).createTime;
			LogUtils.e("shizhong","------------time-"+time);
			LogUtils.e("shizhong",DateUtils.parseTime(time)+"");
			getDynamicNewData(DateUtils.parseTime(time)+"");


		}

	}

	private void getDynamicNewData(final String time){
		Map<String, String> params = new HashMap<String, String>();
		String rootURL = "/video/homeDynamicVideosHaveUpdate";
		params.put("token",login_token);
		params.put("timestamp",time);
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), rootURL, params, new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				LogUtils.e("shizhong","req:-----------------"+req);
				try {
					JSONObject jsonObject=new JSONObject(req);
					int code=jsonObject.getInt("code");
					String msg="";
					switch (code){
						case 100001:
                         JSONObject data=jsonObject.getJSONObject("data");
							boolean hasUpdate=data.getBoolean("haveUpdate");
							if(hasUpdate){
								mDynamicFragment.setHasNewDate(true);
								mTopTagView.setVisibility(View.VISIBLE);
							}else{
								mDynamicFragment.setHasNewDate(false);
								mTopTagView.setVisibility(View.INVISIBLE);
							}
							break;
						case 900001:
							msg = jsonObject.getString("msg");
						default:
							ToastUtils.showErrorToast(getActivity(), code, msg, true);
							break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestFail() {

			}

			@Override
			public void requestNetExeption() {

			}
		},false);
	}



	@Override
	public void onPause() {
		super.onPause();

	}

	public void hide() {
		if (mDynamicFragment != null) {
			mDynamicFragment.onPause();
		}
	}

	private UploadVideoTaskBean mUploadVideoBean;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mDynamicFragment != null) {
			mDynamicFragment.onActivityResult(requestCode, resultCode, data);
		}
		if (mRecodeFragmnet != null) {
			mRecodeFragmnet.onActivityResult(requestCode, resultCode, data);
		}

		if (requestCode == ContantsActivity.Action.ACTION_PUSH_VIDEO) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					task_id = data.getStringExtra(ContantsActivity.Video.VIDEO_TASK_ID);
					mUploadVideoBean = VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id).findTask(task_id);

					String videoPath = mUploadVideoBean.videoPath;
					File videoFile = null;

					if (!TextUtils.isEmpty(videoPath) && (videoFile = new File(videoPath)).exists()) {
						getVideoToken(login_token, BaseQiNiuRequest.MODEL_ACTION_VIDEO, videoFile);
						VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id)
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
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("token", loginToken);
		LogUtils.e("获取视频上传token", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), getVideoTokenTag, params,
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
							default:
								ToastUtils.showErrorToast(getActivity(), code, msg, true);
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showShort(getActivity(), getString(R.string.error_jsonpare));
						}

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(getActivity(), getString(R.string.net_error));

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(getActivity(), getString(R.string.net_error));
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
									VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id)
											.updateVideoUploadState(task_id, videoFileName, 1);
									uploadVideo(task_id);
								}

							} catch (JSONException e) {
								e.printStackTrace();
								ToastUtils.showShort(getActivity(), getString(R.string.error_jsonpare));
							}

						} else {
							ToastUtils.showShort(getActivity(), getString(R.string.error_member_head));
						}

					}
				}, uploadOptions);
	}

	private void uploadVideo(final String task_id) {
		UploadVideoTaskBean bean = VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id).findTask(task_id);
		if (bean == null) {
			return;
		}
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
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), getAddVideoTag, params,
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
								ToastUtils.showErrorToast(getActivity(), code, msg, true);
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
								FileOptionsUtils.removeVideo(getActivity(), memebr_id, task_id);
								VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id)
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
								event.put("time", DateUtils.format9(System.currentTimeMillis()));
								MobclickAgent.onEvent(getActivity(), "video_add_ID", event);

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
						if (getActivity() != null) {
							ToastUtils.showShort(getActivity(), getString(R.string.net_error));
						}
					}

					@Override
					public void requestFail() {
						if (getActivity() != null) {
							ToastUtils.showShort(getActivity(), getString(R.string.net_error));
						}
					}
				}, false);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.recommended_title:
				hide();
				index = 0;
				break;
			case R.id.dynaamic_title:
				mTopTagView.setVisibility(View.GONE);
				index = 1;
				if(mDynamicFragment!=null){
					mDynamicFragment.refreshView();
				}
				break;
		}
			if (currentTabIndex != index) {
				FragmentTransaction trx =getFragmentManager().beginTransaction();
				trx.hide(titleFragments[currentTabIndex]);
				trx.show(titleFragments[index]).commit();
			}
			titles[currentTabIndex].setSelected(false);
			// 把当前tab设为选中状态
			titles[index].setSelected(true);
			currentTabIndex = index;

		}


	OnClickListener onClickListener=new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()){
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
	};

	private Dialog mAlterDialogReUpload;

	private void showAlterDialogReUpload() {
		if (mAlterDialogReUpload == null) {
			if (getActivity() == null) {
				return;
			}
			mAlterDialogReUpload = DialogUtils.confirmDialog(getActivity(), "有视频未上传结束，是否继续上传", " 继续上传", "取消上传",
					new ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							reUploadVideo();
						}

						@Override
						public void onCancleClick(Dialog dialog) {
							delectVideoTask();
						}
					});
		}
		if (!mAlterDialogReUpload.isShowing()) {
			mAlterDialogReUpload.show();
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
				uploadDes = " 重新上传..";
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
		if (getActivity() == null) {
			return;
		}
		if (task_id != null) {
			FileOptionsUtils.removeVideo(getActivity(), memebr_id, task_id);
			VideoUploadTaskDBManager.getInstance(getActivity(), memebr_id).delectedTask(task_id);
			if (mUploadVideoManager != null) {
				mUploadVideoManager = null;
			}
			if (BaseHttpNetMananger.getRequstQueue() != null) {
				BaseHttpNetMananger.getRequstQueue().cancelAll(getAddVideoTag);
			}
		}
	}

	private void showUploadView(String uploadDes) {
		mUploadProgressBar.setMax(102);
		mUploadProgressDes.setText(uploadDes);
		Animation mAppShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mUploadView.startAnimation(mAppShowAction);
		mUploadView.setVisibility(View.VISIBLE);
		if (mUploadFailView != null && mUploadFailView.getVisibility() == View.VISIBLE) {
			mUploadFailView.setVisibility(View.GONE);
		}
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
			if (mUploadFailView != null && mUploadFailView.getVisibility() == View.VISIBLE) {
				mUploadFailView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 上传成功
	 * 
	 * @param
	 */
	private void showUploadSuccessWindow(ShareContentBean sharebean) {
		if (getActivity() == null) {
			return;
		}
		hideUploadView();
		if (mUploadVideoWindow == null) {
			mUploadVideoWindow = new UploadVideoSuccessWindow(getActivity());
		}
		mUploadVideoWindow.setShareBean(sharebean);
		if (!mUploadVideoWindow.isShowing()) {
			mUploadVideoWindow.show(mRootView);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getVideoTokenTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getAddVideoTag);
		}
		if (mAlterDialogReUpload != null) {
			mAlterDialogReUpload = null;
		}
		if (mUploadVideoWindow != null) {
			mUploadVideoWindow = null;
		}
		if (mUploadProgressDes != null) {
			mUploadProgressDes = null;
		}
		System.gc();
	}
}
