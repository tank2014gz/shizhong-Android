package com.shizhong.view.ui.jiequ;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.JieQuSingleClassMusicAdapter;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.bean.MusicObject;

import java.util.ArrayList;


/**
 * Created by yuliyan on 16/8/25.
 */
public class ActivityMusicSingleClass extends BaseUmengActivity implements View.OnClickListener{

    private ImageView mLeftBack;
    private TextView mTitleText;
    private ImageView mSearchText;
    private  ImageView mMoreText;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;
    private JieQuSingleClassMusicAdapter mAdapter;
    private ArrayList<MusicObject> mMusicDatats=new ArrayList<MusicObject>();
    private String titleText;


    @Override
    protected void initView() {
        setContentView(R.layout.activity_music_single_class_layout);
        mLeftBack=(ImageView) findViewById(R.id.left_bt);
        mTitleText=(TextView) findViewById(R.id.title_tv);
        mTitleText.setText(titleText);
        mSearchText=(ImageView)findViewById(R.id.search_btn);
        mMoreText=(ImageView)findViewById(R.id.btn_more);
        mPullToRefreshLayout=((PullToRefreshLayout) findViewById(R.id.refresh_view));
        mListView = (ListView) findViewById(R.id.content_view);
        mAdapter=new JieQuSingleClassMusicAdapter(ActivityMusicSingleClass.this,mMusicDatats);
        mListView.setAdapter(mAdapter);
        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });

    }

    @Override
    protected void initBundle() {
        titleText=getIntent().getStringExtra(ContantsActivity.MUSIC.music_calss_name);
    }

    @Override
    protected void initData() {
        mLeftBack.setOnClickListener(this);
        mSearchText.setOnClickListener(this);
        mMoreText.setOnClickListener(this);
        MusicObject musicObject;
        for (int i=0;i<10;i++){
            musicObject=new MusicObject();
            musicObject.music_name="music"+i;
            mMusicDatats.add(musicObject);
        }
        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.left_bt:
                onBackPressed();
                break;
            case  R.id.search_btn:
                ToastUtils.showShort(ActivityMusicSingleClass.this,"搜索");

                break;
            case  R.id.btn_more:
                ToastUtils.showShort(ActivityMusicSingleClass.this,"更多");
                break;
        }
    }
}
