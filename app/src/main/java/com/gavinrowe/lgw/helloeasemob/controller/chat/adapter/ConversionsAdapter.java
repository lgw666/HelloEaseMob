package com.gavinrowe.lgw.helloeasemob.controller.chat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.activity.ChatActivity;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMConversation;
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
 * 会话适配器
 */

public class ConversionsAdapter extends RecyclerView.Adapter<ConversionsAdapter.ConversionsViewHolder> {

    private List<EMConversation> conversations;
    private List<String> targets;
    private Activity activity;

    public ConversionsAdapter(Activity activity, List<String> targets, List<EMConversation> conversations) {
        this.activity = activity;
        this.targets = targets;
        this.conversations = conversations;
    }

    @Override
    public ConversionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversionsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false));
    }

    @Override
    public void onBindViewHolder(ConversionsViewHolder holder, int position) {
        // 会话目标
        final String target = targets.get(position);
        EMConversation conversion = conversations.get(position);
        // 将最后一条消息显示出来
        EMTextMessageBody body = (EMTextMessageBody) conversion.getLastMessage().getBody();
        String lastMsg = body.getMessage();
        LogUtils.d("会话 最后一条消息：" + lastMsg);
        // 最后一条消息时间
        String lastMsgTime = new SimpleDateFormat("a hh:mm", Locale.getDefault()).format(new Date(conversion.getLastMessage().getMsgTime()));

        holder.tvName.setText(target);
        holder.tvLatestMsg.setText(lastMsg);
        holder.tvTime.setText(lastMsgTime);
        holder.itemConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, ChatActivity.class);
                it.putExtra("target",target);
                activity.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return targets.size();
    }

    static class ConversionsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_latest_msg)
        TextView tvLatestMsg;
        @BindView(R.id.item_conversation)
        RelativeLayout itemConversation;

        ConversionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
