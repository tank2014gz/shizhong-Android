package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.utils.DrawableUitls;
import com.shizhong.view.ui.bean.MusicSortType;

import java.util.List;

/**
 * Created by yuliyan on 16/8/28.
 */
public class MusicSortTypeAdapter extends BaseSZAdapter<MusicSortType,MusicSortTypeAdapter.ViewHodler>{


    public MusicSortTypeAdapter(Context context, List<MusicSortType> list) {
        super(context, list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_mine_music_sort_type_layout;
    }

    @Override
    protected ViewHodler getHodler(View v) {
        ViewHodler hodler=new ViewHodler();
        hodler.sortType=(TextView) v.findViewById(R.id.sort_type);
        return hodler;
    }

    @Override
    protected void initItem(int posiotion, MusicSortType data, ViewHodler holder) {
         if(data!=null){
             String sortTypeName=data.sortTypeName;
             if(TextUtils.isEmpty(sortTypeName)){
                 sortTypeName="null";
             }
             holder.sortType.setText(sortTypeName);
             int sortTypeIcon=data.sortTypeIcon;
             DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(holder.sortType,sortTypeIcon,0,0,0);

            if(data.isSelected){
                holder.sortType.setSelected(true);
            }else{
                holder.sortType.setSelected(false);
            }
         }
    }

    public  static class ViewHodler{

        public TextView sortType;
    }
}
