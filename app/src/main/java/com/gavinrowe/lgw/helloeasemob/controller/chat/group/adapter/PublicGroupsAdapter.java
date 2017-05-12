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
import com.gavinrowe.lgw.helloeasemob.controller.chat.group.activity.JoinGroupActivity;
import com.hyphenate.chat.EMGroupInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/12
 * 公开的群组适配器
 */

public class PublicGroupsAdapter extends RecyclerView.Adapter<PublicGroupsAdapter.PublicGroupsViewHolder> {

    private Activity activity;
    private List<EMGroupInfo> publicGroups;

    public PublicGroupsAdapter(Activity activity, List<EMGroupInfo> publicGroups) {
        this.activity = activity;
        this.publicGroups = publicGroups;
    }

    @Override
    public PublicGroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PublicGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(PublicGroupsViewHolder holder, int position) {
        final EMGroupInfo group = publicGroups.get(position);
        String groupName = group.getGroupName();
        final String groupId = group.getGroupId();
        holder.tvName.setText(groupName);
        holder.itemGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, JoinGroupActivity.class);
                it.putExtra("groupId", groupId);
                activity.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicGroups.size();
    }

    static class PublicGroupsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.item_group)
        LinearLayout itemGroup;

        PublicGroupsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
