package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.bean.MusicObject;

import java.util.List;

/**
 * Created by yuliyan on 16/8/27.
 */
public class MineMusicListAdapter extends BaseSZAdapter<MusicObject,MineMusicListAdapter.ViewHolder>{


    public MineMusicListAdapter(Context context, List<MusicObject> list) {
        super(context, list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_mine_music_layout;
    }

    @Override
    protected ViewHolder getHodler(View v) {
        ViewHolder holder=new ViewHolder();
        holder.music_player=(ImageView) v.findViewById(R.id.music_player_btn);
        holder.music_name=(TextView) v.findViewById(R.id.music_name);
        holder.music_more=(ImageView) v.findViewById(R.id.music_more);
        return holder;
    }

    @Override
    protected void initItem(int posiotion, MusicObject data, ViewHolder holder) {

    }

    static class ViewHolder{
        ImageView music_player;
        TextView music_name;
        ImageView music_more;
    }
}
