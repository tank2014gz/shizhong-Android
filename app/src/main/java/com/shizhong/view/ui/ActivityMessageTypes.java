package com.shizhong.view.ui;

import java.util.ArrayList;
import java.util.List;

import com.shizhong.view.ui.adapter.MessageReplyAdapger;
import com.shizhong.view.ui.adapter.NewFriendsAdapter;
import com.shizhong.view.ui.adapter.NoticMessageAdapter;
import com.shizhong.view.ui.adapter.ThumbUpAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityMessageTypes extends BaseFragmentActivity implements OnClickListener {
	private BaseAdapter mAdapter;
	private ArrayList<MessageInfoExtraBean> mDatas = new ArrayList<MessageInfoExtraBean>();
	private String messageType;
	private String titleName;
	private String memebr_id;
	private ListView listView;
	private int mDelectId;
	private  int mDelectPosition;
	@Override
	protected void initView() {
		setContentView(R.layout.activity_message_list_layout);
		if (messageType.equals(ContantsActivity.Message.MESSAGE_NEW_FRIEND)) {
			titleName = "新朋友";
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_COMMENT)) {
			titleName = "评论";
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_LIKE)) {
			titleName = "点赞";
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_NOTIC)) {
			titleName = "通知";
		}
		((TextView) findViewById(R.id.title_tv)).setText(titleName);
		findViewById(R.id.left_bt).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.content_view);
		if (messageType.equals(ContantsActivity.Message.MESSAGE_NEW_FRIEND)) {
			mAdapter = new NewFriendsAdapter(ActivityMessageTypes.this, mDatas);
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_COMMENT)) {
			mAdapter = new MessageReplyAdapger(ActivityMessageTypes.this, mDatas);
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_LIKE)) {
			mAdapter = new ThumbUpAdapter(ActivityMessageTypes.this, mDatas);
		} else if (messageType.equals(ContantsActivity.Message.MESSAGE_NOTIC)) {
			mAdapter = new NoticMessageAdapter(ActivityMessageTypes.this, mDatas);
		}
		listView.setAdapter(mAdapter);
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mDatas!=null){
					mDelectPosition=i;
					MessageInfoExtraBean bean = mDatas.get(i);
					mDelectId=bean.id;
					showAlterDialog();
					return  true;
				}

				return false;
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				if (messageType.equals(ContantsActivity.Message.MESSAGE_NEW_FRIEND)) {

					MessageInfoExtraBean item=mDatas.get(i);
					String memberId = item.fromId;
					if (!TextUtils.isEmpty(memberId)) {
						mIntent.setClass(ActivityMessageTypes.this, ActivityMemberInfor.class);
						mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
					   startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
						overridePendingTransition(R.anim.dialog_right_in_anim,
								R.anim.dialog_right_out_anim);
					}
				} else if (messageType.equals(ContantsActivity.Message.MESSAGE_COMMENT)) {
					MessageInfoExtraBean video = mDatas.get(i);
						if(video!=null) {
							mIntent.setClass(ActivityMessageTypes.this, VideoPlayerActivity.class);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, video.videoId);
							startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO);
						}
				} else if (messageType.equals(ContantsActivity.Message.MESSAGE_LIKE)) {

					MessageInfoExtraBean video= mDatas.get(i);
					if (video!=null) {
						try {
							JSONObject extraObj = new JSONObject(video.content);
							String videoId = extraObj.getString("id");
							mIntent.setClass(ActivityMessageTypes.this, VideoPlayerActivity.class);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, videoId);
							startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				} else if (messageType.equals(ContantsActivity.Message.MESSAGE_NOTIC)) {
					int next_action=-1;
					MessageInfoExtraBean data = mDatas.get(i);
					if (data != null) {
						try {
							String type = data.targetType;
							String content = data.content;
							JSONObject contentJSON = new JSONObject(content);
							if (ContantsActivity.Message.MESSAGE_RECOMMEND_CLUB.equals(type)) {
								mIntent.setClass(ActivityMessageTypes.this, ActivityClubDetail.class);
								next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_CREW_DETAIL;
								mIntent.putExtra(ContantsActivity.Club.CLUB_ID, contentJSON.getString("id"));
								startActivityForResult(mIntent,next_action);
							} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_PERSON.equals(type)) {
								mIntent.setClass(ActivityMessageTypes.this, ActivityMemberInfor.class);
								next_action=ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO;
								mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, contentJSON.getString("id"));
								startActivityForResult(mIntent,next_action);
							} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_VIDEO.equals(type)) {
								mIntent.setClass(ActivityMessageTypes.this, VideoPlayerActivity.class);
								next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO;
								mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, contentJSON.getString("id"));
								startActivityForResult(mIntent,next_action);
							} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_TOPIC.equals(type)) {
								mIntent.setClass(ActivityMessageTypes.this, ActivityTopicDetail.class);
								next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_TOPIC_DETAIL;
								mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, contentJSON.getString("id"));
								startActivityForResult(mIntent,next_action);
							} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_NEWS.equals(type)) {
								mIntent.setClass(ActivityMessageTypes.this, ActivityNewsWebContent.class);
								mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, contentJSON.getString("newsUrl"));
								startActivityForResult(mIntent,next_action);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}
		});
	}



	private Dialog mDialog;

	private void showAlterDialog() {
		if (mDialog == null) {
			mDialog = DialogUtils.confirmDialog(ActivityMessageTypes.this, "确定删除当前消息", "确定", "取消", new DialogUtils.ConfirmDialog() {

				@Override
				public void onOKClick(Dialog dialog) {
					mDatas.remove(mDelectPosition);
					MessageDBManager
							.getInstance(ActivityMessageTypes.this,
									PrefUtils.getString(ActivityMessageTypes.this, ContantsActivity.LoginModle.LOGIN_USER_ID, ""))
							.delect(mDelectId);
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onCancleClick(Dialog dialog) {
					// TODO Auto-generated method stub

				}
			});
		}
		if (!mDialog.isShowing()) {
			mDialog.show();
		}

	}
	@Override
	protected void initBundle() {
		messageType = getIntent().getStringExtra(ContantsActivity.Message.MESSAGE_TYPE);
		memebr_id = PrefUtils.getString(ActivityMessageTypes.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
	}

	@Override
	protected void initData() {
		Thread loadThread = new Thread(new Runnable() {

			@Override
			public void run() {
				List<MessageInfoExtraBean> messages = MessageDBManager.getInstance(ActivityMessageTypes.this, memebr_id)
						.findTypeMessage(messageType);
				if (messages != null) {
					int len = messages.size();
					for (int i = 0; i < len; i++) {
						mDatas.add(0, messages.get(i));
					}
					mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					});
				}

			}
		});
		loadThread.start();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;

		default:
			break;
		}

	}

}
