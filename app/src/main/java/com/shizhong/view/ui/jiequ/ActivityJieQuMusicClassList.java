package com.shizhong.view.ui.jiequ;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.ActivityMessageTypes;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.BannerHolderView;
import com.shizhong.view.ui.adapter.JiequMusicClassGridAdapter;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.BannerDataPackage;
import com.shizhong.view.ui.bean.DanceClass;
import com.shizhong.view.ui.bean.DanceList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuliyan on 16/8/18.
 */
public class ActivityJieQuMusicClassList extends BaseUmengActivity{

    private View mBannerLayout;
    private String login_token;
    private List<BannerBean> bannerBeanList = new ArrayList<BannerBean>();
    private ScrollView mScrollView;
    @SuppressWarnings("rawtypes")
    private ConvenientBanner mConvenientBanner;
    private ACache mJSONCache;
    private String banner_json_filename = "jiequ_banner";
    private int mBannerWidth, mBannerHeight;

    private GridView mMusicGridView;
    private ArrayList<DanceClass> mDanceList=new ArrayList<DanceClass>();
    private JiequMusicClassGridAdapter mDancesClassAdapter;
    private String getCategoryTag = "/category/getAll";

    @Override
    protected void initView() {
        setContentView(R.layout.activity_jiequ_music_class_list_layout);
        ((TextView)findViewById(R.id.title_tv)).setText(R.string.music);
        mBannerWidth = UIUtils.getScreenWidthPixels(ActivityJieQuMusicClassList.this);
        mBannerHeight = (mBannerWidth / 16) * 6;
        mConvenientBanner = (ConvenientBanner) findViewById(R.id.convenient_banner);
        ViewGroup.LayoutParams lp = mConvenientBanner.getLayoutParams();
        lp.width = mBannerWidth;
        lp.height = mBannerHeight;
        mConvenientBanner.setLayoutParams(lp);
        JiequHttpRequest.requestBanner(ActivityJieQuMusicClassList.this, login_token, "0",
                new JiequHttpRequest.HttpRequstBannerCallBack() {
                    @Override
                    public void callback(String req, final List<BannerBean> list) {
                        int code = 0;
                        String msg = "";

                        try {
                            JSONObject root = new JSONObject(req);
                            code = root.getInt("code");
                            if (code != 100001) {
                                if (code == 900001) {
                                    msg = root.getString("msg");
                                }
                                ToastUtils.showErrorToast(ActivityJieQuMusicClassList.this, code, msg, true);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        refreshBannerInfo(list);
                        mJSONCache.put(banner_json_filename, req);
                    }

                    @Override
                    public void callbackFail() {
                        mConvenientBanner.setVisibility(View.GONE);
                    }
                });
        mScrollView=(ScrollView) findViewById(R.id.scroll_view);
        mScrollView.smoothScrollTo(0,20);
        mMusicGridView=(GridView) findViewById(R.id.music_gird);
        mDancesClassAdapter=new JiequMusicClassGridAdapter(ActivityJieQuMusicClassList.this,mDanceList);
        mMusicGridView.setAdapter(mDancesClassAdapter);
        mMusicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mDanceList!=null&&mDanceList.size()>i){
                    DanceClass  dance= mDanceList.get(i);
                    mIntent.setClass(ActivityJieQuMusicClassList.this,ActivityMusicSingleClass.class);
                    mIntent.putExtra(ContantsActivity.MUSIC.music_calss_name,dance.categoryName);
                    ActivityJieQuMusicClassList.this.startActivityForResult(mIntent,-1);

                }
            }
        });

    }

    private void refreshBannerInfo(final List<BannerBean> list) {
        bannerBeanList = list;
        if (bannerBeanList == null || bannerBeanList.size() == 0) {
            mConvenientBanner.setVisibility(View.GONE);
        } else {
            mConvenientBanner.setVisibility(View.VISIBLE);
        }
        mMainHandler.post(new Runnable() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void run() {
                mConvenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public Object createHolder() {
                        return new BannerHolderView(ActivityJieQuMusicClassList.this, bannerBeanList);
                    }
                }, bannerBeanList).setPageIndicator(
                        new int[] { R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused });
            }
        });
    }

    @Override
    protected void initBundle() {
        mJSONCache = ACache.get(ActivityJieQuMusicClassList.this, "jiequ");
        login_token = PrefUtils.getString(ActivityJieQuMusicClassList.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
    }


    /**
     * 获取titles 标题
     */
    private void getTitles() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", login_token);
        LogUtils.e("获取所有舞蹈种类列表", "-------");
        BaseHttpNetMananger.getInstance(ActivityJieQuMusicClassList.this).postJSON(ActivityJieQuMusicClassList.this, getCategoryTag,
                params, new IRequestResult() {
                    @Override
                    public void requestSuccess(String req) {
                        DanceList list = GsonUtils.json2Bean(req, DanceList.class);
                        int code = list.code;
                        switch (code) {
                            case 100001:
                                break;
                        }
                        mDanceList.clear();
                        mDanceList.addAll(list.data);
                        mDancesClassAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void requestFail() {
                        ToastUtils.showShort(ActivityJieQuMusicClassList.this, getString(R.string.net_error));
                    }

                    @Override
                    public void requestNetExeption() {
                        ToastUtils.showShort(ActivityJieQuMusicClassList.this, getString(R.string.net_conected_error));
                    }
                }, true);

    }

    @Override
    protected void initData() {
        String bannerJSON = mJSONCache.getAsString(banner_json_filename);
        if (!TextUtils.isEmpty(bannerJSON)) {
            int code = 0;
            String msg = "";

            try {
                JSONObject root = new JSONObject(bannerJSON);
                code = root.getInt("code");
                if (code != 100001) {
                    if (code == 900001) {
                        msg = root.getString("msg");
                    }
                    ToastUtils.showErrorToast(ActivityJieQuMusicClassList.this, code, msg, true);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            BannerDataPackage dataPackage = GsonUtils.json2Bean(bannerJSON, BannerDataPackage.class);
            refreshBannerInfo(dataPackage.data);
        }
        getTitles();
    }
}
