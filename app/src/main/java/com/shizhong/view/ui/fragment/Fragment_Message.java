package com.shizhong.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.shizhong.view.ui.ActivityAddFriends;
import com.shizhong.view.ui.LoginActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.MessageAdapter;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.MessageTypeListItem;
import com.hyphenate.easeui.utils.PrefUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.hyphenate.easeui.ContantsActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_Message extends EaseBaseFragment implements View.OnClickListener {
	private final static int MSG_REFRESH = 2;
	private boolean isLogin;
	private TextView mLoginBtn;

	private List<MessageTypeListItem> headDatas = new ArrayList<MessageTypeListItem>();
	private List<EMConversation> mFootDatas = new ArrayList<EMConversation>();

	private String[] iconNameList = null;
	private int[] iconList = null;

	private ListView listView;
	private BaseAdapter mAdapter;
	private ArrayList<Object> mDatas = new ArrayList<Object>();
	private int[] hasMessagesNoRead = new int[] { 0, 0, 0, 0 };
	protected boolean hidden;
	protected boolean isConflict;
	private String login_token;
	private Intent mIntent = new Intent();
	private TextView mAddFriendBtn;

	protected Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				break;
			case 1:
				break;

			case MSG_REFRESH: {
//
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			}
			default:
				break;
			}
		}
	};

	public void initBundle() {
		isLogin = PrefUtils.getBoolean(getActivity(), ContantsActivity.LoginModle.IS_LOGIN, false);
		login_token = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	private void initHeadDatas() {
		mDatas.clear();
		headDatas.clear();
		mFootDatas.clear();
		int len = iconNameList.length;
		MessageTypeListItem bean = null;
		for (int i = 0; i < len; i++) {
			bean = new MessageTypeListItem();
			bean.iconId = iconList[i];
			bean.iconName = iconNameList[i];
			bean.type = (i + 1000) + "";
			bean.hasNoRead = hasMessagesNoRead[i];
			headDatas.add(bean);
		}
		if (headDatas != null) {
			mDatas.addAll(headDatas);
		}
//		mFootDatas.addAll(loadConversationList());
//		mDatas.addAll(mFootDatas);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		loadConversationList();
	}

	@Override
	protected void setUpView() {
		initHeadDatas();

	}

	// protected EMConversationListener convListener = new
	// EMConversationListener() {
	//
	// @Override
	// public void onCoversationUpdate() {
	// refreshHx();
	// }
	//
	// };

	public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
			Bundle savedInstanceState) {
		EMChatManager.getInstance().loadAllConversations();
		return inflater.inflate(R.layout.fragment_message, container, false);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
			return;
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void initView() {
		initBundle();
		if (isLogin) {
			getView().findViewById(R.id.login_layout).setVisibility(View.GONE);
			iconNameList = getResources().getStringArray(R.array.message);
			iconList = new int[] { R.drawable.new_friend_icon, R.drawable.like_icon, R.drawable.comment_icon,
					R.drawable.notice_icon };
			mAddFriendBtn = (TextView) getView().findViewById(R.id.add_firends);
			mAddFriendBtn.setVisibility(View.VISIBLE);
			mAddFriendBtn.setOnClickListener(this);
			listView = (ListView) getView().findViewById(R.id.content_view);
			mAdapter = new MessageAdapter(getActivity(), mDatas);
			listView.setAdapter(mAdapter);

		} else {
			getView().findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
			mLoginBtn = (TextView) getView().findViewById(R.id.login_btn);
			mLoginBtn.setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login_btn:
			 mIntent.setClass(getActivity(), LoginActivity.class);
			 break;
		case R.id.add_firends:
			 mIntent.setClass(getActivity(), ActivityAddFriends.class);
			 break;
		}
		startActivityForResult(mIntent, -1);
	}

	/**
	 * 刷新环信消息列表
	 */
	public void refreshHx() {
		loadConversationList();

	}

	/**
	 * 刷新极光推送的消息列表
	 */
	public void refreshJPush() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refreshHx();
			refreshJPush();
		}
		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden && !isConflict) {
			refreshHx();
			refreshJPush();
		}
	}

	/**
	 * 获取会话列表
	 * 
	 * @param
	 * @return +
	 */
	protected void loadConversationList() {
		// 获取所有会话，包括陌生人
		Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					// if(conversation.getType() !=
					// EMConversationType.ChatRoom){
					String ext = conversation.getExtField();
					String memberId = conversation.getUserName();
					if (TextUtils.isEmpty(ext)) {
						getLittleMemberInfo(memberId, conversation, sortList);
					} else {
						sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(),
								conversation));
					}
				}
			}
		}
		try {
			sortConversationByLastChatTime(sortList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}

		        mDatas.removeAll(mFootDatas);
				mFootDatas.clear();
				mFootDatas.addAll(list);
				LogUtils.e("SHIZHONG","footlistsize"+mFootDatas.size());
				mDatas.addAll(mFootDatas);

		if(handler.hasMessages(MSG_REFRESH)){
			handler.removeMessages(MSG_REFRESH);
		}
		handler.sendEmptyMessage(MSG_REFRESH);
//		return list;
	}

	private void getLittleMemberInfo(String memberId, final EMConversation conversation,
			final List<Pair<Long, EMConversation>> sortList) {
		String rootUrl = "/member/getLittleMemberInfo";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("memberId", memberId);
		LogUtils.e("获取用户信息", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), rootUrl, params, new IRequestResult() {

			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject root = new JSONObject(req);
					int code = root.getInt("code");
					if (code == 100001) {
						String data = root.getString("data");
						conversation.setExtField(data);
						sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(),
								conversation));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void requestNetExeption() {
				// TODO Auto-generated method stub

			}

			@Override
			public void requestFail() {
				// TODO Auto-generated method stub

			}
		}, false);

	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (isConflict) {
			outState.putBoolean("isConflict", true);
		}
	}

	public interface CallBackJPushMessage {
		public boolean isHasJPushMessage();
	}

}
