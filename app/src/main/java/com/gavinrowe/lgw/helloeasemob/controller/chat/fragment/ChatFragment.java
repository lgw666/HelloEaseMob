package com.gavinrowe.lgw.helloeasemob.controller.chat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.activity.ChatActivity;
import com.gavinrowe.lgw.helloeasemob.controller.chat.adapter.ConversionsAdapter;
import com.gavinrowe.lgw.helloeasemob.listener.GroupListener;
import com.gavinrowe.lgw.helloeasemob.listener.MessageListener;
import com.gavinrowe.lgw.helloeasemob.model.cache.CachePreferences;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/8
 * 会话
 */

public class ChatFragment extends Fragment {
    @BindView(R.id.rv_conversations)
    RecyclerView rvConversations;
    @BindView(R.id.et_target)
    EditText etTarget;
    // 会话目标
    private List<String> targets = new ArrayList<>();
    private List<EMConversation> conversations = new ArrayList<>();
    // 会话适配器
    private ConversionsAdapter conversionsAdapter;
    // 消息监听器
    private MessageListener messageListener;
    // 群组监听
    private GroupListener groupListener;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MessageListener.EM_ACTION_GET_MSG.equals(action)) {
                getAllConversations();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        messageListener = new MessageListener(getContext());
        groupListener = new GroupListener(getContext());
        // 设置消息监听
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
        // 设置群组监听
        EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);

        conversionsAdapter = new ConversionsAdapter(getActivity(), targets, conversations);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        rvConversations.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvConversations.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rvConversations.setAdapter(conversionsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllConversations();
        IntentFilter filter = new IntentFilter(MessageListener.EM_ACTION_GET_MSG);
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void getAllConversations() {
        targets.clear();
        conversations.clear();
        Map<String, EMConversation> c = EMClient.getInstance().chatManager().getAllConversations();
        for (Map.Entry<String, EMConversation> entry : c.entrySet()) {
            targets.add(entry.getKey());
            conversations.add(entry.getValue());
        }
        conversionsAdapter.notifyDataSetChanged();

        LogUtils.d("会话个数：" + targets.size());
        LogUtils.d("会话数据：" + targets);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    // 发起会话点击事件
    @OnClick(R.id.btn_create_conversation)
    public void onViewClicked() {
        String target = etTarget.getText().toString().toLowerCase();
        if (target.isEmpty()) {
            Toast.makeText(getActivity(), "请输入目标用户名！", Toast.LENGTH_SHORT).show();
        } else if (target.equals(CachePreferences.getUserInfo().getUn())) {
            Toast.makeText(getContext(), "不能向自己发起会话！", Toast.LENGTH_SHORT).show();
        } else {
            Intent it = new Intent(getContext(), ChatActivity.class);
            it.putExtra("target", target);
            startActivity(new Intent(it));
            etTarget.setText("");
        }
    }

}
