package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.DanceClass;

import java.util.List;

/**
 * Created by yuliyan on 16/8/23.
 */
public class JiequMusicClassGridAdapter extends BaseSZAdapter<DanceClass,JiequMusicClassGridAdapter.ViewHolder>{


    private int item_width;
    public JiequMusicClassGridAdapter(Context context, List<DanceClass> list) {
        super(context, list);
        item_width= UIUtils.getScreenWidthPixels(context)/2-UIUtils.dipToPx(context,20);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_layout_jiequ_music_item_layout;
    }

    @Override
    protected ViewHolder getHodler(View v) {
        ViewHolder holder=new ViewHolder();
        holder.music_bg_image=(SimpleDraweeView) v.findViewById(R.id.music_forgound_view);
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) holder.music_bg_image.getLayoutParams();
        layoutParams.width=item_width;
        layoutParams.height=item_width;
        holder.music_bg_image.setLayoutParams(layoutParams);
        holder.music_class_name=(TextView) v.findViewById(R.id.music_class_name);
        return holder;
    }

    @Override
    protected void initItem(int posiotion, DanceClass data, ViewHolder holder) {
      if(data!=null){
          String imageUrl=data.fileUrl;
          if(TextUtils.isEmpty(imageUrl)){
              imageUrl="";
          }
          holder.music_bg_image.setImageURI(Uri.parse(imageUrl));

          String  music_class_name=data.categoryName;
          if(TextUtils.isEmpty(music_class_name)){
              music_class_name="";
          }
          holder.music_class_name.setText(music_class_name);
      }
    }

    public static class ViewHolder{
        SimpleDraweeView music_bg_image;
        TextView music_class_name;
    }
}
