package com.gavinrowe.lgw.helloeasemob.listener;

import android.app.Activity;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/8
 * 环信连接状态监听
 */

public class ConnectionListener implements EMConnectionListener {
    private Activity activity;

    public ConnectionListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(final int errorCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (errorCode == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    Toast.makeText(activity, "账户被删除", Toast.LENGTH_SHORT).show();
                } else if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    Toast.makeText(activity, "账户在另外一台设备登录", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtils.hasNetwork(activity))
                        // 连接不到聊天服务器
                        Toast.makeText(activity, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    else
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(activity, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
