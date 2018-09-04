package com.shizhong.view.ui.base.net;

import android.content.Context;
import cn.jpush.android.api.TagAliasCallback;

import com.easemob.chat.EMChatManager;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.UserExtendsInfoDataPakage;
import com.shizhong.view.ui.jpush.JpushUtils;
import com.umeng.analytics.MobclickAgent;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuliyan on 16/1/26.
 */
public class VideoHttpRequest {
	public interface HttpRequestCallBack {
		public void callFailBack();
	}

	public interface HttpRequestCallBack1 {
		public void callBack(UserExtendsInfo info);

		public void callBackFail();
	}

	public static void voteVideo(final Context context, final String videoId, final String type, final int position,
			String login_token, final HttpRequestCallBack callBack) {

		String rootUrl = "/video/like";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoId", videoId);
		params.put("type", type);
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				LogUtils.i("like", req);
				try {
					int code = new JSONObject(req).getInt("code");
					if (code != 100001) {
						callBack.callFailBack();
						if (type.equals("1")) {
							ToastUtils.showShort(context, context.getString(R.string.video_vote_fail));
						} else {
							ToastUtils.showShort(context, context.getString(R.string.video_vode_cancle_fail));
						}
						return;
					}

					EMChatManager.getInstance().loadAllConversations();
					if (type.equals("1")) {
						Map<String, String> event = new HashMap<String, String>();
						event.put("video_id", videoId);
						event.put("type", "视频点赞量");
						event.put("time", DateUtils.format9(System.currentTimeMillis()));
						MobclickAgent.onEvent(context, "video_like_ID", event);
						ToastUtils.showShort(context, context.getString(R.string.video_vote_ok));
					} else {
						ToastUtils.showShort(context, context.getString(R.string.video_vote_cancle));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestFail() {
				ToastUtils.showShort(context, context.getString(R.string.net_error));
				callBack.callFailBack();
			}

			@Override
			public void requestNetExeption() {
				ToastUtils.showShort(context, context.getString(R.string.net_conected_error));
				callBack.callFailBack();
			}
		}, false);

	}

	public static void getMememberInfo(String rootUrl,final Context context, String token, String memberId, final boolean isMe,
			final HttpRequestCallBack1 callBack) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("memberId", memberId);
		LogUtils.i("userInfo", params.toString());
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject root = new JSONObject(req);
					int code = root.getInt("code");
					String msg = null;
					if (code != 100001) {
						if (code == 900001) {
							msg = root.getString("msg");
						}
						ToastUtils.showErrorToast(context, code, msg, true);
						callBack.callBackFail();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UserExtendsInfoDataPakage dataPakage = GsonUtils.json2Bean(req, UserExtendsInfoDataPakage.class);
				UserExtendsInfo info = dataPakage.data;
				if (info != null) {
					if (isMe) {
						if (info != null) {
							PrefUtils.putString(context, ContantsActivity.LoginModle.HEAD_URL, info.headerUrl);
							PrefUtils.putString(context, ContantsActivity.LoginModle.NICK_NAME, info.nickname);
							PrefUtils.putString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, info.memberId);
							PrefUtils.putString(context, ContantsActivity.LoginModle.BIRTHDAY, info.birthday);
							// 设置极光推送的别名和tag值
							Set<String> tags = new HashSet<String>();
							String sex=null;
							if(info.sex.equals("0")){
								sex="sex_0";
								tags.add("sex_0");
							}else if(info.sex.equals("1")){
								tags.add("sex_1");
								sex="sex_1";
							}
							
							if (DebugConfig.is_debug) {
								tags.add("develop");
							} else {
								// tags.add("isProduction");
							}

							JpushUtils.getInstance(context).setAliasAndTags(context, info.memberId, tags,
									new TagAliasCallback() {
										@Override
										public void gotResult(int arg0, String arg1, Set<String> arg2) {
											LogUtils.i("jpush", "arg0:{" + arg0 + "},arg1:{" + arg1 + "},");
										}

									});

                            String[] getuitags=null;
							if(DebugConfig.is_debug){
								getuitags=new String[]{sex,"develop","android"};
							}else{
								getuitags=new String[]{sex,"android"};
							}

							Tag[] tagParam = new Tag[getuitags.length];
							for (int i = 0; i < getuitags.length; i++) {
								Tag t = new Tag();
								t.setName(getuitags[i]);
								tagParam[i] = t;
							}

							int i = PushManager.getInstance().setTag(context, tagParam, System.currentTimeMillis() + "");
							String text = "ERROR";
							switch (i) {
								case PushConsts.SETTAG_SUCCESS:
									text = "设置标签成功";
									break;

								case PushConsts.SETTAG_ERROR_COUNT:
									text = "设置标签失败, tag数量过大, 最大不能超过200个";
									break;

								case PushConsts.SETTAG_ERROR_FREQUENCY:
									text = "设置标签失败, 频率过快, 两次间隔应大于1s";
									break;

								case PushConsts.SETTAG_ERROR_REPEAT:
									text = "设置标签失败, 标签重复";
									break;

								case PushConsts.SETTAG_ERROR_UNBIND:
									text = "设置标签失败, 服务未初始化成功";
									break;

								case PushConsts.SETTAG_ERROR_EXCEPTION:
									text = "设置标签失败, 未知异常";
									break;

								case PushConsts.SETTAG_ERROR_NULL:
									text = "设置标签失败, tag 为空";
									break;

								case PushConsts.SETTAG_NOTONLINE:
									text = "还未登陆成功";
									break;

								case PushConsts.SETTAG_IN_BLACKLIST:
									text = "该应用已经在黑名单中,请联系售后支持!";
									break;

								case PushConsts.SETTAG_NUM_EXCEED:
									text = "已存 tag 超过限制";
									break;

								default:
									break;
							}
							LogUtils.e("shizhong gettui_tag","-----------"+text+"-------------");
							PrefUtils.putString(context, ContantsActivity.LoginModle.SEX, info.sex);
							PrefUtils.putString(context, ContantsActivity.LoginModle.SIGNTURE, info.signature);
							PrefUtils.putString(context, ContantsActivity.LoginModle.PROVINCE, info.provinceId);
							PrefUtils.putString(context, ContantsActivity.LoginModle.CITY, info.cityId);
							PrefUtils.putString(context, ContantsActivity.LoginModle.DISTRICTID, info.districtId);
							PrefUtils.putString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, info.memberId);
							LogUtils.i("userInfo",
									PrefUtils.getString(context, ContantsActivity.LoginModle.HEAD_URL, ""));
							LogUtils.i("userInfo",
									PrefUtils.getString(context, ContantsActivity.LoginModle.NICK_NAME, ""));

						}
						callBack.callBack(info);
					} else {
						if (isMe) {
							callBack.callBackFail();
						} else {
							if (info != null) {
								callBack.callBack(info);
							}
						}
					}

				}
			}

			@Override
			public void requestFail() {
				ToastUtils.showShort(context, context.getString(R.string.net_error));
			}

			@Override
			public void requestNetExeption() {
				ToastUtils.showShort(context, context.getString(R.string.net_conected_error));
			}
		}, false);
	}

	public interface HttpRequestCallBack2 {
		public void callBackFail();
	}

	/**
	 * 视频点赞
	 * 
	 * @param context
	 * @param login_token
	 * @param memberId
	 * @param position
	 * @param isAtt
	 * @param callback
	 */

	public static void addAttention(final Context context, String login_token, String memberId, final int position,
			final String isAtt, final HttpRequestCallBack2 callback) {
		String rootUrl = "/member/attention";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("memberId", memberId);
		params.put("type", isAtt);
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject json = new JSONObject(req);
					int code = json.getInt("code");
					String msg = null;
					if (code != 100001) {
						if (code == 900001) {
							msg = json.getString("msg");
						}
						ToastUtils.showErrorToast(context, code, msg, true);
						callback.callBackFail();
					}

				} catch (JSONException e) {
					callback.callBackFail();
					e.printStackTrace();
				}

			}

			@Override
			public void requestFail() {
				callback.callBackFail();
				ToastUtils.showShort(context, context.getString(R.string.net_error));
			}

			@Override
			public void requestNetExeption() {
				callback.callBackFail();
				ToastUtils.showShort(context, context.getString(R.string.net_conected_error));
			}
		}, false);
	}

	public interface HttpRequestCallBack3 {
		public void callBackFail();
	}

	/**
	 * 视频评论点赞
	 * 
	 * @param context
	 * @param login_token
	 * @param commentId
	 * @param type
	 * @param callback
	 */
	public static void addLikeReply(final Context context, String login_token, String commentId, String type,
			final HttpRequestCallBack3 callback) {
		String rootUrl = "/video/commentLike";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("commentId", commentId);
		params.put("type", type);
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject json = new JSONObject(req);
					int code = json.getInt("code");
					String msg = null;
					if (code != 100001) {
						if (code == 900001) {
							msg = json.getString("msg");
						}
						ToastUtils.showErrorToast(context, code, msg, true);
						callback.callBackFail();
					}

				} catch (JSONException e) {
					callback.callBackFail();
					e.printStackTrace();
				}

			}

			@Override
			public void requestFail() {
				callback.callBackFail();
				ToastUtils.showShort(context, context.getString(R.string.net_error));
			}

			@Override
			public void requestNetExeption() {
				callback.callBackFail();
				ToastUtils.showShort(context, context.getString(R.string.net_conected_error));
			}
		}, false);
	}
}
