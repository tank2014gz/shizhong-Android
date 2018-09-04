package com.shizhong.view.ui;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.adapter.FragmentAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.MyRadioGroup;
import com.shizhong.view.ui.fragment.NearByPeopleFragment;
import com.shizhong.view.ui.fragment.NearByVideoFragment;
import com.shizhong.view.ui.map.ILoactionInfoCaller;
import com.shizhong.view.ui.map.SZLocationManager;
import com.shizhong.view.ui.map.SZLocationManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuliyan on 16/7/24.
 */
public class ActivityJiequNearBy  extends BaseFragmentActivity implements View.OnClickListener,ILoactionInfoCaller {

    private MyRadioGroup mNeayByRadioGroup;
    private ImageView mBackImage;
    private ViewPager noViewPager;

    private ProgressBar mLoadingProgressBar;
    private String login_token;


    private NearByVideoFragment nearVideoFragment;
    private NearByPeopleFragment mNearPeopleFragment;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mNearByAdapter;

    private boolean isFinishLocation;


    private SZLocationManager mGeoMapManager;
    @Override
    protected void initBundle() {
        super.initBundle();
        login_token = PrefUtils.getString(ActivityJiequNearBy.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
        mGeoMapManager = SZLocationManagerFactory.getLoactionManager(ActivityJiequNearBy.this,
                SZLocationManagerFactory.TYPE_GAODE);
    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_nearby_layout);
        mBackImage = (ImageView) findViewById(R.id.left_bt);
        mBackImage.setOnClickListener(this);
        mNeayByRadioGroup = (MyRadioGroup) findViewById(R.id.rg_nearby);
        noViewPager = (ViewPager) findViewById(R.id.nearby_viewpager);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading_dialog_progressBar);


        nearVideoFragment = new NearByVideoFragment();
        fragmentList.add(nearVideoFragment);
        mNearPeopleFragment = new NearByPeopleFragment();
        fragmentList.add(mNearPeopleFragment);
        mNearByAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        noViewPager.setAdapter(mNearByAdapter);

        mNeayByRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_club:
//                        ToastUtils.showShort(ActivityJiequNearBy.this,"附近视频");
                        noViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_people:
//                        ToastUtils.showShort(ActivityJiequNearBy.this,"附近人");
                        if (nearVideoFragment != null) {
                            nearVideoFragment.onPause();
                        }
                        noViewPager.setCurrentItem(1);
                        break;
                }
            }
        });
        mNeayByRadioGroup.check(R.id.rb_club);
        noViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mNeayByRadioGroup.check(R.id.rb_club);
                        break;
                    case 1:
                        mNeayByRadioGroup.check(R.id.rb_people);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mGeoMapManager.setLoacionInfoListener(this);
        mGeoMapManager.startLoacation();
    }

    /**
     * 定位回调方法
     * @param province
     * @param city
     * @param lng
     * @param lat
     */
    @Override
    public void callback(String province, String city, String lng, String lat) {
        // TODO Auto-generated method stub
        if(!isFinishLocation){
            isFinishLocation=true;
            LogUtils.e("shizhong","------lng"+lng+",---lat"+lat);
            editUserLoaction(lng, lat);
            mGeoMapManager.stopLoaction();
            if (nearVideoFragment != null) {
                nearVideoFragment.refreshVideoLoactionInfo(province, city, lng, lat);
            }
            if (mNearPeopleFragment != null) {
                mNearPeopleFragment.refreshPeopleLocationInfo(province, city, lng, lat);
            }
        }}

    @Override
    protected void initData() {
        super.initData();
    }



    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (nearVideoFragment != null) {
            nearVideoFragment.hide();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_bt:
                finish();
                break;
        }
    }


    private void editUserLoaction(String lng, String lat) {
        final String baseURL = "/member/modifyMemberInfo";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", login_token);
        params.put("lng", lng);
        params.put("lat", lat);
        LogUtils.e("修改个人资料", "-------");
        BaseHttpNetMananger.getInstance(ActivityJiequNearBy.this).postJSON(ActivityJiequNearBy.this, baseURL, params,
                new IRequestResult() {
                    @Override
                    public void requestSuccess(String req) {
                        mLoadingProgressBar.setVisibility(View.GONE);
                        try {
                            JSONObject root = new JSONObject(req);
                            int code = root.getInt("code");
                            String msg = null;
                            switch (code) {
                                case 100001:

                                    break;
                                case 900001:
                                    msg = root.getString("msg");
                                default:
                                    ToastUtils.showErrorToast(ActivityJiequNearBy.this, code, msg, true);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void requestFail() {
                        mLoadingProgressBar.setVisibility(View.GONE);
                        ToastUtils.showShort(ActivityJiequNearBy.this, getString(R.string.net_error));
                    }

                    @Override
                    public void requestNetExeption() {
                        mLoadingProgressBar.setVisibility(View.GONE);
                        ToastUtils.showShort(ActivityJiequNearBy.this, getString(R.string.net_conected_error));

                    }
                }, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGeoMapManager!=null) {
            mGeoMapManager.onDestory();
            mGeoMapManager=null;
        }
        if(mNearByAdapter!=null){
            mNearByAdapter=null;
        }
        if(nearVideoFragment!=null){
            nearVideoFragment=null;

        }
        if(mNearPeopleFragment!=null){
            mNearPeopleFragment=null;
        }
        if(fragmentList!=null){
            fragmentList.clear();
            fragmentList=null;
        }
        System.gc();
    }
}
