package com.shizhong.view.ui.base.net;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.NetworkUtils;

import java.util.Map;

public class BaseHttpNetMananger {
	public static final String TAG = "BaseVolleryRequest";
	private static BaseHttpNetMananger mInstance;
	private static RequestQueue mRequestQueue;

	// //测试后台接口（内网）
	// private static final String Production_URL_HOST =
	// "http://api.shizhongapp.com:28686";
	private static final String Production_URL_HOST = "http://api.shizhongapp.com";
	private static final String TEST_URL_HOST = "http://120.24.88.112:8999/api";
	// private static final String TEST_URL_HOST =120.24.88.112
	// "http://120.24.88.112:8999/api/";
	private static final String TEST_LOCO_URL = "http://192.168.1.116:8080/api";
	private static final String TEST_LOCOL_ZHAO_URL = "http://192.168.1.113:8080/api/";
	// // 测试后台接口(外网)
	public static String current_URL = TEST_URL_HOST;

	private ACache mCacheManager;
	private ICacheFileNameCallBack mCacheCallBack;

	private BaseHttpNetMananger(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mCacheManager = ACache.get(context);
	}

	public static BaseHttpNetMananger getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BaseHttpNetMananger(context);
		}
		return mInstance;
	}

	public static RequestQueue getRequstQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		}
		return null;
	}

	public StringRequest postJSON(final Context context, String path1, final Map<String, String> map,
			final IRequestResult result, final boolean isCache) {
		if (DebugConfig.is_local_zhao) {
			current_URL = TEST_LOCOL_ZHAO_URL;
		} else {
			if (DebugConfig.is_local) {
				current_URL = TEST_LOCO_URL;
			} else {
				if (DebugConfig.is_debug) {
					current_URL = TEST_URL_HOST;
				} else {
					current_URL = Production_URL_HOST;
				}
			}
		}
		final String url = current_URL + path1;
		String resultString = null;
		final String cacheName = url + map.toString();
//		LogUtils.e("shizhong http ", map.toString());
//        LogUtils.e("http url",url);
		if (isCache) {
			if (mCacheManager != null) {
				resultString = mCacheManager.getAsString(cacheName);
			}
			if (mCacheCallBack != null) {
				mCacheCallBack.callName(cacheName);
			}
			if (resultString != null && result != null) {

				result.requestSuccess(resultString);
				return null;
			}
		}
		if (!NetworkUtils.isNetworkConnected(context) && result != null) {
			result.requestNetExeption();
			return null;
		}

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// response
				// System.out.println("++++++"+response);
//				LogUtils.e("shizhong http", " url---->"+url+"<-----"+response);
				if (isCache && mCacheManager != null) {
					mCacheManager.put(cacheName, response);
				}
				if (result != null) {
					result.requestSuccess(response);
				}
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtils.e("shizhong http", error.toString());
				if (result != null) {
					result.requestFail();
				}
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				return map;
			}

		};
		postRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000,0,0f));
		postRequest.setTag(path1);
		mRequestQueue.add(postRequest);
		return postRequest;
	}

	public BaseHttpNetMananger addCacheFileCallBack(ICacheFileNameCallBack cacheCallBack) {
		mCacheCallBack = cacheCallBack;
		return mInstance;
	}

	public interface ICacheFileNameCallBack {
		void callName(String name);
	}

}
