package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsContentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuliyan on 16/6/26.
 */
public class EventsAndNewsAAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private  static  final  int TYPE_HEADER=1;
    private  static  final  int TYPE_ITEM=2;
    private  static  final  int TYPE_FOOTER=3;

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    public  static  final int LOAD_NO_MORE=2;

    //加载到底了

    //上拉加载更多状态-默认为0
    private int load_more_status=0;

//    private View headView;
    private Context mContext;
    private List<EventsNewsBean> mLists;
    private Intent mIntent=new Intent();
    public EventsAndNewsAAdapter(Context context, List<EventsNewsBean> lists){
        super();
        this.mContext=context;
        this.mLists=lists;
    }

//    public void setHeadView(View view){
//        headView=view;
//    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.item_view:
        EventsNewsContentBean bean = (EventsNewsContentBean) v.getTag(R.string.app_name);
        if (bean != null) {
            mIntent.setClass(mContext, ActivityNewsWebContent.class);
            mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, bean.url);
            ((Activity) mContext).startActivityForResult(mIntent, -1);
        }
        break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1==getItemCount()){
            return TYPE_FOOTER;
        }else if(position==0){
            return TYPE_HEADER;
        }else {
            return TYPE_ITEM;
        }
    }



    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_layout_envents_news, parent, false);
            EventsAndNewsAViewHolder holder = new EventsAndNewsAViewHolder(v);
            return  holder;
        }else if(viewType==TYPE_FOOTER){
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.newapi_load_more_layout,parent,false);
            FootViewHolder holder=new FootViewHolder(v);
            return holder;
        }else  if( viewType==TYPE_HEADER){
             View headView=LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_jiqu_banner_layout,null,false);
              HeaderViewHolder headerViewHolder=new HeaderViewHolder(headView);
            return headerViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  EventsAndNewsAViewHolder) {
            EventsAndNewsAViewHolder holder1=(EventsAndNewsAViewHolder) holder;
            EventsNewsBean data = mLists.get(position-1);
            String newsCover = data.coverUrl;
            if (TextUtils.isEmpty(newsCover)) {
                newsCover = "";
            } else {
                newsCover = FormatImageURLUtils.formatURL(newsCover, ContantsActivity.Image.SMALL,
                        ContantsActivity.Image.SMALL);
            }
            holder1.newsCover.setImageURI(Uri.parse(newsCover));
            String newsContent = data.title;
            if (TextUtils.isEmpty(newsContent)) {
                newsContent = "";
            }
            holder1.newContent.setText(newsContent);
            String time = DateUtils.formateVideoCreateTime(data.createTime);
            if (TextUtils.isEmpty(time)) {
                time = "";
            }
            holder1.newsTime.setText(time);
            holder1.itemViews.setTag(R.string.app_name, data.content);
            holder1.itemViews.setOnClickListener(EventsAndNewsAAdapter.this);
        }else if(holder instanceof  FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            switch (load_more_status){

                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_iv.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    RotateAnimation refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(footViewHolder.foot_view_item_iv.getContext(), R.anim.rotating);
                    LinearInterpolator lir = new LinearInterpolator();
                    refreshingAnimation.setInterpolator(lir);
                    footViewHolder.foot_view_item_iv.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view_item_iv.startAnimation(refreshingAnimation);
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    break;
                case LOAD_NO_MORE:
                    footViewHolder.foot_view_item_iv.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText("已经到底了");
                    break;
            }
        }else if(holder instanceof  HeaderViewHolder){

        }
    }

    @Override
    public int getItemCount() {
        return mLists.size()+2;
    }


    public static  class EventsAndNewsAViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView newsCover;
        TextView newContent;
        TextView newsTime;
        View itemViews;

        public EventsAndNewsAViewHolder(View itemView) {
            super(itemView);
            newsCover = (SimpleDraweeView) itemView.findViewById(R.id.news_cover);
            newContent = (TextView) itemView.findViewById(R.id.news_content);
            itemViews = itemView.findViewById(R.id.item_view);
            newsTime = (TextView) itemView.findViewById(R.id.news_time);
        }
    }

    public  static  class FootViewHolder extends RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        private ImageView foot_view_item_iv;

        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.loadstate_tv);
            foot_view_item_iv=(ImageView) view.findViewById(R.id.loading_icon);
        }
    }


    public  static  class HeaderViewHolder extends RecyclerView.ViewHolder{

        public  ConvenientBanner mConvenientBanner;
        private List<BannerBean> bannerBeanList = new ArrayList<BannerBean>();
        private Handler mMainHandler=new Handler();
        private String login_token;
        private  int mBannerWidth;
        private  int mBannerHeight;
        public HeaderViewHolder(final  View view) {
            super(view);
            mBannerWidth = UIUtils.getScreenWidthPixels(view.getContext());
            mBannerHeight = (mBannerWidth / 16) * 6;
            login_token = PrefUtils.getString(view.getContext(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
            mConvenientBanner = (ConvenientBanner) view.findViewById(R.id.convenient_banner);
            mConvenientBanner.getLayoutParams().height=mBannerHeight;
            JiequHttpRequest.requestBanner(view.getContext(), login_token, "0",
                new JiequHttpRequest.HttpRequstBannerCallBack() {
                    @Override
                    public void callback(String req, final List<BannerBean> list) {
                        if (list == null || list.size() <= 0) {
                            mConvenientBanner.setVisibility(View.GONE);
                        } else {
                            mConvenientBanner.setVisibility(View.VISIBLE);
                        }

                        bannerBeanList = list;
                        LogUtils.e("banner", bannerBeanList.toString());
                        mMainHandler.post(new Runnable() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void run() {
                                mConvenientBanner.setPages(new CBViewHolderCreator() {
                                    @Override
                                    public Object createHolder() {
                                        return new BannerHolderView(view.getContext(), bannerBeanList);
                                    }
                                }, bannerBeanList).setPageIndicator(new int[] { R.drawable.ic_page_indicator,
                                        R.drawable.ic_page_indicator_focused });
                            }
                        });
                    }

                    @Override
                    public void callbackFail() {
                        mConvenientBanner.setVisibility(View.GONE);

                    }
                });
        }
    }
}
