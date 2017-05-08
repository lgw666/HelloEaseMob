package com.gavinrowe.lgw.helloeasemob;

import android.app.Application;

import com.gavinrowe.lgw.helloeasemob.model.cache.CachePreferences;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/8
 * Application
 */

public class HelloEaseMobApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CachePreferences.init(this);
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
}
