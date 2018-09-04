package com.shizhong.view.ui.base.view.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.MusicSortTypeAdapter;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.MusicSortType;

import java.util.ArrayList;

/**
 * Created by yuliyan on 16/8/27.
 */
public class MusicSortTypeWindow extends PopupWindow implements View.OnClickListener{
    private Context mContext;
    private View mRootView;
    private ListView listView;
    private MusicSortTypeAdapter mSortTypeAdapter;
    private ArrayList<MusicSortType> mSortTypeList=new ArrayList<>();
    private String[] sortTypeNames=new String[]{"默认","下载量","点赞量"};
    private int [] sortTyoeIcons=new int[]{R.drawable.icon_sort_default_count,R.drawable.icon_sort_download_count,R.drawable.icon_sort_like_count};

    private SortTypeCallBack mSortCallBack;
    public interface  SortTypeCallBack{

         void typeCallBack(int type);

    }

    public void  setSortCallBack(SortTypeCallBack callBack){
        this.mSortCallBack=callBack;
    }



    public MusicSortTypeWindow(Context context) {
        this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public MusicSortTypeWindow(Context context, int width, int height) {
        this.mContext = context;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(width);
        setHeight(height);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.mine_music_type_menu_window_layout, null);
        mRootView.setOnClickListener(this);
        initView(mRootView);
        setContentView(mRootView);
//		this.setAnimationStyle(R.style.dialog_anim_bottom);
    }

    private void initView(View mRootView) {
        listView=(ListView) mRootView.findViewById(R.id.type_list);
        mSortTypeAdapter=new MusicSortTypeAdapter(mContext,mSortTypeList);
        listView.setAdapter(mSortTypeAdapter);
        MusicSortType sortType=null;
        for (int i=0;i<3;i++){
            sortType=new MusicSortType();
            sortType.sortType=i;
            sortType.sortTypeName=sortTypeNames[i];
            sortType.sortTypeIcon=sortTyoeIcons[i];
            if(i==0){
                sortType.isSelected=true;
            }
            mSortTypeList.add(sortType);
        }
        mSortTypeAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j=0;j<mSortTypeList.size();j++){
                    if(i!=j) {
                        mSortTypeList.get(j).isSelected = false;
                    }else{
                        mSortTypeList.get(j).isSelected=true;
                    }
                }

                mSortTypeAdapter.notifyDataSetChanged();
                dismiss();
                if(mSortCallBack!=null){
                    mSortCallBack.typeCallBack(i);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    public void show(View view) {

        showAtLocation(view, Gravity.CENTER,0, -UIUtils.dipToPx(mContext,80));
    }
}
