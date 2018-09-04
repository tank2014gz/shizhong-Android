package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.bean.Image;
import com.shizhong.view.ui.bean.MusicObject;

import java.util.List;

/**
 * Created by yuliyan on 16/8/26.
 */
public class JieQuSingleClassMusicAdapter extends BaseSZAdapter<MusicObject,JieQuSingleClassMusicAdapter.ViewHolder>{


    public JieQuSingleClassMusicAdapter(Context context, List<MusicObject> list) {
        super(context, list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_single_class_music_layout;
    }

    @Override
    protected ViewHolder getHodler(View v) {
        ViewHolder holder=new ViewHolder();
        holder.playMusic=(ImageView)v.findViewById(R.id.music_player_btn);
        holder.musicName=(TextView)v.findViewById(R.id.music_name);
        holder.musicLike=(TextView)v.findViewById(R.id.music_like_count);
        holder.music_option_more=(ImageView)v.findViewById(R.id.music_more);
        return holder;
    }

    @Override
    protected void initItem(int posiotion, MusicObject data, ViewHolder holder) {

    }

    public  static class ViewHolder{
        ImageView playMusic;
        TextView musicName;
        TextView musicLike;
        ImageView music_option_more;
    }

}
