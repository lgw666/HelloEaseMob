package com.gavinrowe.lgw.helloeasemob.model.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.chat.EMClient;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/8
 * 保存配置
 */

public class CachePreferences {
    private static final String NAME = CachePreferences.class.getSimpleName();
    private static final String UN = "USER_NAME";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    private CachePreferences() {
    }

    @SuppressLint("CommitPrefEdits")
    public static void init(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 清空本地存储的数据
     */
    public static void clearAllData() {
        editor.clear();
        editor.apply();
    }

    /**
     * 存储数据
     *
     * @param userInfo 用户信息
     */
    public static void setUserInfo(UserInfo userInfo) {
        editor.putString(UN, userInfo.getUn());
        editor.apply();
    }

    /**
     * 获取数据
     */
    public static UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUn(preferences.getString(UN, null));
        return userInfo;
    }

    /**
     * 获取登陆状态
     */
    public static boolean getSignInStatus() {
        return EMClient.getInstance().isLoggedInBefore();
    }
}
