package com.shizhong.view.ui.base;

/**
 * Created by yuliyan on 16/6/26.
 */
public class SystemSetting {
    private   static SystemSetting systemSetting;

    public  static  boolean isUseHotRandom;//热门是否随机刷新
    public  static  boolean isUseCategoryRandom;//舞种是否使用随机刷新
    private SystemSetting(){
        super();
    }

    public SystemSetting getSetting(){
        if(systemSetting==null){
            systemSetting=new SystemSetting();
        }
        return  systemSetting;
    }




}
