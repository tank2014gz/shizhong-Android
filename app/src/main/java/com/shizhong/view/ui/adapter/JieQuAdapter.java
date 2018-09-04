package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityCrewList;
import com.shizhong.view.ui.ActivityEvenetsAndNews;
import com.shizhong.view.ui.ActivityJiequNearBy;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.ActivityRankinglist;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.ActivityTopicList;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.MyGridView;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsContentBean;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.TopicDataPackage;
import com.shizhong.view.ui.jiequ.ActivityJieQuMusicClassList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/27.
 */
public class JieQuAdapter extends BaseAdapter implements View.OnClickListener {

	private Context mContext;
	private List<EventsNewsBean> mList;
	private LayoutInflater mLayoutInflater;
	private String login_token;
	private Intent mIntent = new Intent();
	private ACache mJSONCache;
	private final String topic_json_filename = "jiequ_topics";

	private  ArrayList<TopicBean> topicBeanArrayList=new ArrayList<>();

	public JieQuAdapter(Context context, List<EventsNewsBean> list) {
		this.mContext = context;
		login_token = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		this.mList = list;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public void setCacherManager(ACache jSONCache) {
		this.mJSONCache = jSONCache;
	}

	@Override
	public int getCount() {
		return 4 + mList.size();
	}

	@Override
	public Object getItem(int i) {
		return mList == null ? null : mList.get(i) == null ? null : mList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (position == 0) {
			holder = new ViewHolder();
			convertview = mLayoutInflater.inflate(R.layout.jiequ_item_first_layout_v1, null);
			holder.crew = (ImageView) convertview.findViewById(R.id.crew);
			holder.crew.setOnClickListener(this);
			holder.rankingList=(ImageView) convertview.findViewById(R.id.rankingList);
			holder.rankingList.setOnClickListener(this);
			holder.nearby = (ImageView) convertview.findViewById(R.id.nearby);
			holder.nearby.setOnClickListener(this);
			holder.music=(ImageView) convertview.findViewById(R.id.music_icon);
			holder.music.setOnClickListener(this);
		}

		if (position == 1) {
			holder = new ViewHolder();
			convertview = mLayoutInflater.inflate(R.layout.jiequ_item_topic_head_layout, null);
			holder.topicMore = (TextView) convertview.findViewById(R.id.topic_more);
			holder.topicMore.setTag(R.string.app_name, position);
			holder.topicMore.setOnClickListener(this);
		}

		if (position == 2) {
			holder = new ViewHolder();
			convertview = mLayoutInflater.inflate(R.layout.jiequ_item_topic_list_layout, null);
			holder.topicGirdView = (MyGridView) convertview.findViewById(R.id.topic_grid);
			final BaseAdapter adapter = new JieQuTopicAdapter(mContext, topicBeanArrayList);
			holder.topicGirdView.setAdapter(adapter);
			holder.topicGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					if(topicBeanArrayList!=null&&topicBeanArrayList.size()>i){
						TopicBean bean = topicBeanArrayList.get(i);
						if (bean != null) {
							String topicId = bean.topicId;
							String topicName = bean.topicName;
							mIntent.setClass(mContext, ActivityTopicDetail.class);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, "#" + topicName + "#");
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, topicId);
							((Activity) mContext).startActivityForResult(mIntent, -1);
						}
					}
				}
			});
			String topicJSON = mJSONCache.getAsString(topic_json_filename);
			if (!TextUtils.isEmpty(topicJSON)) {
				int code = 0;
				String msg = "";

				try {
					JSONObject root = new JSONObject(topicJSON);
					code = root.getInt("code");
					if (code != 100001) {
						if (code == 900001) {
							msg = root.getString("msg");
						}
						ToastUtils.showErrorToast(mContext, code, msg, true);
					} else {
						TopicDataPackage dataPackage = GsonUtils.json2Bean(topicJSON, TopicDataPackage.class);
						topicBeanArrayList.clear();
						topicBeanArrayList.addAll(dataPackage.data);
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			JiequHttpRequest.requestTopicList(mContext, login_token, 1, 10, new JiequHttpRequest.HttpRequestCallBack() {
				@Override
				public void callback(String req, List<TopicBean> list) {
					if (!TextUtils.isEmpty(req)) {
						int code = 0;
						String msg = "";

						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(mContext, code, msg, true);
							} else {
								topicBeanArrayList.clear();
								topicBeanArrayList.addAll(list);
								adapter.notifyDataSetChanged();
								mJSONCache.put(topic_json_filename, req);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			});

		}
		if (position == 3) {
			holder = new ViewHolder();
			convertview = mLayoutInflater.inflate(R.layout.jiequ_item_topic_head_layout, null);
			holder.topicMore = (TextView) convertview.findViewById(R.id.topic_more);
			holder.topicLable = (TextView) convertview.findViewById(R.id.topic_lable);
			holder.topicLable.setText("赛事 | 资讯");
			holder.topicMore.setTag(R.string.app_name, position);
			holder.topicMore.setOnClickListener(this);
		}

		if (position >= 4) {
			holder = new ViewHolder();
			convertview = mLayoutInflater.inflate(R.layout.jiequ_list_item_events_news_layout, null);
			holder.newsTitle = (TextView) convertview.findViewById(R.id.new_title);
			holder.newsTime = (TextView) convertview.findViewById(R.id.news_time);
			holder.newsCount = (TextView) convertview.findViewById(R.id.news_counts);
			holder.newsImage = (SimpleDraweeView) convertview.findViewById(R.id.news_image);
			holder.itemView = convertview.findViewById(R.id.item_view);
			holder.itemView.setOnClickListener(this);
			if (mList.size() > 1) {
				convertview.setVisibility(View.VISIBLE);
				EventsNewsBean bean = (EventsNewsBean) mList.get(position - 4);
				LogUtils.i("bean", bean.toString());
				if (bean != null) {
					String newTitle = bean.title;
					if (TextUtils.isEmpty(newTitle)) {
						newTitle = "热门资讯";
					}
					holder.newsTitle.setText(newTitle);

					String newTime = DateUtils.formateVideoCreateTime(bean.createTime);
					holder.newsTime.setText(newTime);

					String newsImage = bean.coverUrl;
					if (TextUtils.isEmpty(newsImage)) {
						newsImage = "";
					} else {
						newsImage = FormatImageURLUtils.formatURL(newsImage, ContantsActivity.Image.SMALL,
								ContantsActivity.Image.SMALL);
					}
					holder.newsImage.setImageURI(Uri.parse(newsImage));
					// imageLoad.displayImage(newsImage, holder.newsImage,
					// options);
//					Glide.with(mContext).load(newsImage).placeholder(R.drawable.banner_default_icon)
//							.error(R.drawable.banner_default_icon).transform(new GlideRoundTransform(mContext, 5))
//							.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.newsImage);
				}
				holder.itemView.setTag(R.string.app_name, bean.content);
			} else {
				convertview.setVisibility(View.GONE);
			}

		}

		return convertview;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.topic_more:
			int position = (Integer) view.getTag(R.string.app_name);
			switch (position) {
			case 1:
				mIntent.setClass(mContext, ActivityTopicList.class);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_TOPIC_LIST);
				break;
			case 3:
				mIntent.setClass(mContext, ActivityEvenetsAndNews.class);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_NEWS_LIST);
				break;
			}
			break;
		case R.id.crew:
			mIntent.setClass(mContext, ActivityCrewList.class);
			((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_CLUBS_LIST);

			break;
		case R.id.rankingList:
				mIntent.setClass(mContext, ActivityRankinglist.class);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_RANK_LIST);
				break;
		case R.id.nearby:
			mIntent.setClass(mContext, ActivityJiequNearBy.class);
			((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_NERABY_LIST);
			break;
		case R.id.music_icon:
			mIntent.setClass(mContext, ActivityJieQuMusicClassList.class);
			((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_MUSIC_LIST);

				break;
		case R.id.item_view:
			EventsNewsContentBean bean = (EventsNewsContentBean) view.getTag(R.string.app_name);
			if (bean != null) {
				mIntent.setClass(mContext, ActivityNewsWebContent.class);
				mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, bean.url);
				((Activity) mContext).startActivityForResult(mIntent, -1);
			}
			break;


		}
	}

	class ViewHolder {
//		TextView crew;
//		TextView nearby;
		ImageView crew;
		ImageView rankingList;
		ImageView nearby;
		ImageView music;
		TextView topicMore;
		TextView topicLable;
		MyGridView topicGirdView;
		View itemView;
		TextView newsTitle;
		TextView newsTime;
		TextView newsCount;
		SimpleDraweeView newsImage;

	}

}
