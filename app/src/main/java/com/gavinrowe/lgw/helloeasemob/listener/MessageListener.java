package com.gavinrowe.lgw.helloeasemob.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/9
 * 消息监听
 */

public class MessageListener implements EMMessageListener {
    /**
     * 收到消息
     */
    public static final String EM_ACTION_GET_MSG = "GET_MSG";
    private static final Intent INTENT_GET_MSG = new Intent(EM_ACTION_GET_MSG);

    private LocalBroadcastManager localBroadcastManager;

    public MessageListener(Context context) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //收到消息
        LogUtils.d("消息监听 收到消息：" + messages.size());
        localBroadcastManager.sendBroadcast(INTENT_GET_MSG);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //收到透传消息
        LogUtils.d("消息监听 收到透传消息");

    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        //收到已读回执
        LogUtils.d("消息监听 收到已读回执");

    }

    @Override
    public void onMessageDelivered(List<EMMessage> message) {
        //收到已送达回执
        LogUtils.d("消息监听 收到已送达回执");

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        //消息状态变动
        LogUtils.d("消息监听 消息状态变动");

    }
}
