package com.gavinrowe.lgw.helloeasemob.controller.chat.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.activity.ChatActivity;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMClient;
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
    // 标志，是否删除会话的同时删除聊天记录
    private boolean isDeleteMsgRecord;

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
    public void onBindViewHolder(ConversionsViewHolder holder, final int position) {
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
                it.putExtra("target", target);
                activity.startActivity(it);
            }
        });
        // 长按删除会话
        holder.itemConversation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] choices = {"不删除聊天记录", "删除聊天记录"};
                new AlertDialog
                        .Builder(activity)
                        .setTitle("删除会话")
                        .setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        LogUtils.d("删除会话：不删除聊天记录");
                                        isDeleteMsgRecord = false;
                                        break;
                                    case 1:
                                        LogUtils.d("删除会话：删除聊天记录");
                                        isDeleteMsgRecord = true;
                                        break;
                                }
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d("确定删除会话");
                                deleteConversation(target, position);
                            }
                        })
                        .setNegativeButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });
    }

    // 删除会话
    private void deleteConversation(String target, int position) {
        LogUtils.d("删除会话的位置：" + position);
        //删除和某个user会话，如果需要保留聊天记录，传false
        EMClient.getInstance().chatManager().deleteConversation(target, isDeleteMsgRecord);
        targets.remove(position);
        conversations.remove(position);
        notifyDataSetChanged();
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
