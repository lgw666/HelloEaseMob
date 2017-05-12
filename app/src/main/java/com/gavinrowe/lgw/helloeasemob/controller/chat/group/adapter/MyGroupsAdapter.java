package com.gavinrowe.lgw.helloeasemob.controller.chat.group.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.activity.ChatActivity;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/12
 * 我的群组适配器
 */

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.MyGroupsViewHolder> {

    private Activity activity;
    private List<EMGroup> myGroups;

    public MyGroupsAdapter(Activity activity, List<EMGroup> myGroups) {
        this.activity = activity;
        this.myGroups = myGroups;
    }

    @Override
    public MyGroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(MyGroupsViewHolder holder, int position) {
        final EMGroup group = myGroups.get(position);
        // 群聊名字
        final String groupName = group.getGroupName();
        // 群聊ID
        final String groupId = group.getGroupId();
        holder.tvName.setText(groupName);
        holder.itemGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("进入群聊: " + group.getGroupName() + ", 群聊ID: " + groupId);
                Intent it = new Intent(activity, ChatActivity.class);
                it.putExtra("target", groupId);
                it.putExtra("chatType", ChatActivity.GROUP_CHAT);
                it.putExtra("groupName", groupName);
                activity.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myGroups.size();
    }

    static class MyGroupsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.item_group)
        LinearLayout itemGroup;

        MyGroupsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
