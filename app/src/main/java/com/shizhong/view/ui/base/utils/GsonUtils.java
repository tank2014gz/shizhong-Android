package com.shizhong.view.ui.base.utils;

import com.google.gson.Gson;

/**
 * json解析工具类
 *
 * @author Administrator
 */
public class GsonUtils {
    public static <T> T json2Bean(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
