package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.utils.DrawableUitls;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.view.RoundImageView;
import com.shizhong.view.ui.bean.RankingBean;

import java.util.List;

/**
 * Created by yuliyan on 16/7/3.
 */
public class RankListAdapter extends BaseSZAdapter<RankingBean,RankListAdapter.ViewHolder> implements View.OnClickListener{

    private PlaceDBManager mPlaceDBManager;
    public RankListAdapter(Context context, List<RankingBean> list) {
        super(context, list);
        mPlaceDBManager = new PlaceDBManager(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_ranking_list_layout;
    }

    @Override
    protected ViewHolder getHodler(View v) {
        ViewHolder holder=new ViewHolder();
        holder.rankNum=(TextView) v.findViewById(R.id.rank_num);
        holder.rankName=(TextView) v.findViewById(R.id.rank_name);
        holder.rankAddress=(TextView) v.findViewById(R.id.rank_address);
        holder.goodRankHead=(SimpleDraweeView) v.findViewById(R.id.good_head_image);
        holder.commonRankHead=(SimpleDraweeView) v.findViewById(R.id.commont_head_image);
        holder.rankTagImage=(ImageView) v.findViewById(R.id.rank_tag_image);
        holder.rootView= v.findViewById(R.id.root_view);
        return holder;
    }

    @Override
    protected void initItem(int posiotion, RankingBean data, ViewHolder holder) {
        data.position = posiotion;
        String imageUrl = data.headerUrl;
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "";
        } else {
            imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.SMALL,
                    ContantsActivity.Image.SMALL);
        }
        if(posiotion>2){
            holder.goodRankHead.setVisibility(View.INVISIBLE);
            holder.commonRankHead.setVisibility(View.VISIBLE);
            holder.commonRankHead.setImageURI(Uri.parse(imageUrl));
        }else{
            holder.goodRankHead.setVisibility(View.VISIBLE);
            holder.commonRankHead.setVisibility(View.INVISIBLE);
            holder.goodRankHead.setImageURI(Uri.parse(imageUrl));
        }

        if(posiotion==0){
            holder.rankTagImage.setImageResource(R.drawable.rank_list_1);
        }else if(posiotion==1){
            holder.rankTagImage.setImageResource(R.drawable.rank_list_2);
        }else if (posiotion==2){
            holder.rankTagImage.setImageResource(R.drawable.rank_list_3);
        }else{
            holder.rankTagImage.setImageResource(R.drawable.rank_list_4);
        }


        String num=(posiotion+1)+"";
        holder.rankNum.setText(num);
        String rankName=data.nickname;
        if(TextUtils.isEmpty(rankName)){
            rankName="";
        }
        holder.rankName.setText(rankName);

        String sex=data.sex;
        if(sex!=null&&sex.equals("1")){
            DrawableUitls.setCompoundDrawablesWithIntrinsicBounds( holder.rankName,0,0,R.drawable.man_icon,0);
        }else{
            DrawableUitls.setCompoundDrawablesWithIntrinsicBounds( holder.rankName,0,0,R.drawable.girl_icon,0);
        }
        String cityId = data.cityId;
        String provinceId = data.provinceId;
        String districtId = data.districtId;
        String city = mPlaceDBManager.getCity(cityId);
        String provice = mPlaceDBManager.getProvice(provinceId);
        String zone = mPlaceDBManager.getZone(districtId);
        LogUtils.e("shizhong",city+"--"+provice+"--"+zone);
        LogUtils.e("shizhong",cityId+"--"+provinceId+"--"+districtId);
        if (TextUtils.isEmpty(city) && TextUtils.isEmpty(provice) && TextUtils.isEmpty(zone)) {
            holder.rankAddress.setText("舞者位置信息不详");
        } else {
            StringBuilder address = new StringBuilder();


            if (!TextUtils.isEmpty(provice)&&!TextUtils.isEmpty(city)&&provice.equals(city)) {
                address.append(provice);
            } else {
                if(!TextUtils.isEmpty(provice)){
                    address.append(provice+" ");
                }
                if(!TextUtils.isEmpty(city)){
                    address .append(city+" ");
                }

            }
            if (!TextUtils.isEmpty(zone)) {
                address.append(" ").append(zone);
            }
            holder.rankAddress.setText(address.toString());
        }
        holder.rootView.setTag(data);
        holder.rootView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.root_view:
                RankingBean data= (RankingBean) v.getTag();
                if(data!=null) {
                    mIntent.setClass(mContext, ActivityMemberInfor.class);
                    mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, data.memberId);
                    ((Activity)mContext).startActivityForResult(mIntent,ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
                }
                    break;
        }
    }

    class ViewHolder{
       TextView rankNum;
        TextView rankName;
        TextView rankAddress;
        SimpleDraweeView goodRankHead;
        SimpleDraweeView commonRankHead;
        ImageView rankTagImage;
        View rootView;
    }

}
