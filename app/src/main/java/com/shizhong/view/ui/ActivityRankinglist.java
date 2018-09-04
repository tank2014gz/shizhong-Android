package com.shizhong.view.ui;

import android.view.View;
import android.widget.ListView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.adapter.RankListAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.bean.RankingBean;
import com.shizhong.view.ui.bean.RankingDataPackage;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.UserExtendsListDataPakage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuliyan on 16/7/3.
 */
public class ActivityRankinglist  extends BaseFragmentActivity implements View.OnClickListener{

    private ListView mListView;
    private RankListAdapter mListAdapter;
    private ArrayList<RankingBean> mDatas=new ArrayList<RankingBean>();
     private  String login_token;
    @Override
    protected void initBundle() {
        super.initBundle();
        login_token = PrefUtils.getString(ActivityRankinglist.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_ranking_layout);
        findViewById(R.id.left_bt).setOnClickListener(this);
        mListView=(ListView)findViewById(R.id.content_view);
        mListAdapter=new RankListAdapter(ActivityRankinglist.this,mDatas);
        mListView.setAdapter(mListAdapter);
    }




    @Override
    protected void initData() {
        super.initData();
        getRankList();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_bt:
                onBackPressed();
                break;
        }
    }


    private void  getRankList(){
        final String baseURL = "/member/videoCoutTop";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", login_token);
        params.put("recordNum", DebugConfig.BASE_PAGE_SIZE+"");
        LogUtils.e("获取排行榜", "-------");
        showLoadingDialog();
        BaseHttpNetMananger.getInstance(ActivityRankinglist.this).postJSON(ActivityRankinglist.this, baseURL, params,
                new IRequestResult() {
                    @Override
                    public void requestSuccess(String req) {
                       dismissLoadingDialog();
                        int code = 0;
                        String msg = "";

                        try {
                            JSONObject root = new JSONObject(req);
                            code = root.getInt("code");
                            if (code != 100001) {
                                if (code == 900001) {
                                    msg = root.getString("msg");
                                }
                                ToastUtils.showErrorToast(ActivityRankinglist.this, code, msg, true);
                                return;
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            return;
                        }

                        RankingDataPackage data = GsonUtils.json2Bean(req, RankingDataPackage.class);
                        if (data == null) {
                            return;
                        }
                        List<RankingBean> list = data.data;
                        if (list == null || list.size() <= 0) {
                            return;
                        }
                        mDatas.clear();
                        mDatas.addAll(list);
                        mListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestFail() {
                       dismissLoadingDialog();
                        ToastUtils.showShort(ActivityRankinglist.this, getString(R.string.net_error));
                    }

                    @Override
                    public void requestNetExeption() {
                        dismissLoadingDialog();
                        ToastUtils.showShort(ActivityRankinglist.this, getString(R.string.net_conected_error));

                    }
                }, false);
    }
}
