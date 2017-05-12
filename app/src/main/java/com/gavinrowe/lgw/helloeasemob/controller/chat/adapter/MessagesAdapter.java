package com.gavinrowe.lgw.helloeasemob.controller.chat.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.model.cache.CachePreferences;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/9
 * 消息适配器
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    // 目标，自己
    private static final int TARGET = 0, SELF = 1;


    private Activity activity;
    private List<EMMessage> messages;

    public MessagesAdapter(Activity activity, List<EMMessage> messages) {
        this.activity = activity;
        this.messages = messages;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TARGET == viewType)
            return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg_target, parent, false));

        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg_self, parent, false));
    }

    @SuppressWarnings("all")
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        EMMessage message = messages.get(position);
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String from = message.getFrom();
        String time = new SimpleDateFormat("a hh:mm", Locale.getDefault()).format(new Date(message.getMsgTime()));
        LogUtils.d("消息来源：" + from);
        String msg = body.getMessage();
        // 判断聊天类型
        EMMessage.ChatType chatType = message.getChatType();
        if (chatType == EMMessage.ChatType.GroupChat && TARGET == getItemViewType(position)) {
            holder.tvTarget.setVisibility(View.VISIBLE);
            holder.tvTarget.setText(from);
        }
        holder.tvMsg.setText(msg);
        holder.tvTime.setText(time);
    }

    @Override
    public int getItemViewType(int position) {
        if (CachePreferences.getUserInfo().getUn().equals(messages.get(position).getFrom()))
            return SELF;
        return TARGET;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time)
        TextView tvTime;

        @BindView(R.id.tv_msg)
        TextView tvMsg;

        @Nullable
        @BindView(R.id.tv_target)
        TextView tvTarget;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
