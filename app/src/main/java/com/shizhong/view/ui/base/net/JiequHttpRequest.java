package com.shizhong.view.ui.base.net;

import android.content.Context;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.BannerDataPackage;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsDataPackage;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.TopicDataPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/27.
 */
public class JiequHttpRequest {

	public interface HttpRequstBannerCallBack {
		void callback(String req, List<BannerBean> list);

		void callbackFail();
	}

	public static void requestBanner(final Context context, String token, String positionId,
			final HttpRequstBannerCallBack callBack) {
		String rootUrl = "/banner/getBanners";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("positionId", positionId);
		LogUtils.i("banner", params.toString());
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
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
						ToastUtils.showErrorToast(context, code, msg, true);
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				BannerDataPackage dataPackage = GsonUtils.json2Bean(req, BannerDataPackage.class);
				List<BannerBean> bannerBean = dataPackage.data;
				if (callBack != null) {
					callBack.callback(req, bannerBean);
				}
			}

			@Override
			public void requestFail() {
				if (callBack != null) {
					callBack.callbackFail();
				}
				ToastUtils.showShort(context, context.getString(R.string.net_error));
			}

			@Override
			public void requestNetExeption() {
				if (callBack != null) {
					callBack.callbackFail();
				}
				ToastUtils.showShort(context, context.getString(R.string.net_conected_error));
			}
		}, false);

	}

	/**
	 * 获取话题
	 */
	public interface HttpRequestCallBack {
		void callback(String req, List<TopicBean> list);
	}

	public static void requestTopicList(final Context context, String token, int nowPage, int recordNum,
			final HttpRequestCallBack callback) {
		String rootUrl = "/topic/getTopics";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.i("topic", params.toString());
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
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
						ToastUtils.showErrorToast(context, code, msg, true);
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				TopicDataPackage dataPackage = GsonUtils.json2Bean(req, TopicDataPackage.class);
				List<TopicBean> list = dataPackage.data;

				if (callback != null) {
					callback.callback(req, list);
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

	/**
	 * 获取资讯
	 */
	public interface HttpRequestNewsCallBack {
		void callback(String req, List<EventsNewsBean> list);
	}

	public static void requestEventsAndNews(final Context context, String token, String type, int nowPage,
			int recoderNum, final HttpRequestNewsCallBack callBack) {
		String rootUrl = "/news/getList";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recoderNum + "");
		params.put("type", type);
		BaseHttpNetMananger.getInstance(context).postJSON(context, rootUrl, params, new IRequestResult() {
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
						ToastUtils.showErrorToast(context, code, msg, true);
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				
				EventsNewsDataPackage dataPackage = GsonUtils.json2Bean(req, EventsNewsDataPackage.class);
				List<EventsNewsBean> list = dataPackage.data;
				if (callBack != null) {
					callBack.callback(req, list);
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
}
