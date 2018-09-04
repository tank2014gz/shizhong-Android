package com.shizhong.view.ui.mine;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.MineMusicListAdapter;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.window.MusicSortTypeWindow;
import com.shizhong.view.ui.bean.MusicObject;
import com.shizhong.view.ui.fragment.Fragment_Bottom_Music;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by yuliyan on 16/8/26.
 */
public class ActivityMineMusicList extends BaseUmengActivity implements View.OnClickListener{

    private ImageView mLeftBack;
    private TextView mTitleText;
    private ImageView mSearchText;
    private  ImageView mMoreText;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;
    private RadioGroup mMusicRadioGroup;
//    private RadioButton mLikeBtn;
//    private RadioButton mLocalBtn;
    private MineMusicListAdapter mineMusicListAdapter;
    private ArrayList<MusicObject> mLikedMusicList=new ArrayList<>();
    private ArrayList<MusicObject> mLocalMusicList=new ArrayList<>();

    private MusicSortTypeWindow musicSortTypeWindow;
    private Fragment_Bottom_Music mBottomFragment;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_mine_music_list_layout);
        mLeftBack=(ImageView) findViewById(R.id.left_bt);
        mLeftBack.setImageResource(R.drawable.icon_music_back);
        mLeftBack.setOnClickListener(this);
        mTitleText=(TextView) findViewById(R.id.title_tv);
        mSearchText=(ImageView)findViewById(R.id.search_btn);
        mSearchText.setOnClickListener(this);
        mMoreText=(ImageView)findViewById(R.id.btn_more);
        mMoreText.setOnClickListener(this);
        mPullToRefreshLayout=((PullToRefreshLayout) findViewById(R.id.refresh_view));
        mListView = (ListView) findViewById(R.id.content_view);
        mineMusicListAdapter=new MineMusicListAdapter(ActivityMineMusicList.this,mLikedMusicList);
        mListView.setAdapter(mineMusicListAdapter);
        mMusicRadioGroup=(RadioGroup) findViewById(R.id.music_radio_group);
//        mLikeBtn=(RadioButton) findViewById(R.id.like_music);
//        mLocalBtn=(RadioButton) findViewById(R.id.local_music);
        mMusicRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        musicSortTypeWindow=new MusicSortTypeWindow(ActivityMineMusicList.this, (int)(UIUtils.getScreenWidthPixels(ActivityMineMusicList.this)*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        musicSortTypeWindow.setSortCallBack(new ActivityMineMusicList.SortTypeCallBack());

        mBottomFragment= new Fragment_Bottom_Music();
        getSupportFragmentManager().beginTransaction().add(R.id.bottom_bar, mBottomFragment).show(mBottomFragment).commit();
    }

    static class SortTypeCallBack implements MusicSortTypeWindow.SortTypeCallBack{

        @Override
        public void typeCallBack(int type) {
            LogUtils.e("--------","type:"+type);

        }
    }


    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener=new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i){
                case R.id.like_music:
                    ToastUtils.showShort(ActivityMineMusicList.this,"喜欢");
                    break;
                case R.id.local_music:
                    ToastUtils.showShort(ActivityMineMusicList.this,"本地");
                    break;
            }

        }
    };

    @Override
    protected void initBundle() {

    }

    @Override
    protected void initData() {
        MusicObject musicObject=null;
        for (int i=0;i<10;i++){
            musicObject=new MusicObject();
            musicObject.music_name="music "+i;
            mLikedMusicList.add(musicObject);
        }
        mineMusicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.left_bt:
                onBackPressed();
                break;
            case R.id.search_btn:

                break;
            case R.id.btn_more:
                if(musicSortTypeWindow!=null&&!musicSortTypeWindow.isShowing()){
                    musicSortTypeWindow.show(view);
                }
                break;
        }
    }
}
