package com.gavinrowe.lgw.helloeasemob.controller.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.adapter.MessagesAdapter;
import com.gavinrowe.lgw.helloeasemob.listener.MessageListener;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {
    public static int CHAT = 0, GROUP_CHAT = 1, CHAT_ROOM = 2;


    @BindView(R.id.rv_messages)
    RecyclerView rvMessages;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    // 会话类型, 默认单聊
    private int chatType;

    // 目标
    private String target;
    // 会话对象
    private EMConversation conversation;

    private List<EMMessage> mMessages = new ArrayList<>();
    private MessagesAdapter messagesAdapter;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MessageListener.EM_ACTION_GET_MSG.equals(action)) {
                getMessages();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent it = getIntent();
        target = it.getStringExtra("target");
        LogUtils.d("聊天target：" + target);
        chatType = it.getIntExtra("chatType", CHAT);
        conversation = EMClient.getInstance().chatManager().getConversation(target);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        messagesAdapter = new MessagesAdapter(this, mMessages);
        setToolbar();
        setRefreshLayout();
        setRecyclerView();
        getMessages();
    }


    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 如果会话是新的就获取一次
                if (conversation != null) {
                    // 获取所有显示的消息的数量
                    int size = mMessages.size();
                    // 获取本地存储会话的全部消息数目
                    int localSize = conversation.getAllMsgCount();
                    LogUtils.d("刷新前 本地存储会话的全部聊天记录条数：" + localSize);
                    // 判断信显示消息的数量是否和聊天记录数目相等
                    if (size == localSize) {
                        Toast.makeText(ChatActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    } else {
                        // 获取最后一条消息之前的记录
                        List<EMMessage> messages = conversation.loadMoreMsgFromDB(null, Integer.MAX_VALUE);
                        mMessages.clear();
                        mMessages.addAll(0, messages);
                        messagesAdapter.notifyDataSetChanged();
                        LogUtils.d("刷新后 所有聊天记录条数：" + mMessages.size());
                    }
                }
                refreshLayout.setRefreshing(false);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MessageListener.EM_ACTION_GET_MSG);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void setRecyclerView() {
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messagesAdapter);
    }

    // 发送按钮点击事件
    public void onSendClick(View view) {
        String content = etContent.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "内容不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
            EMMessage message = EMMessage.createTxtSendMessage(content, target);
            etContent.setText("");

            //判断聊天类型，设置chat type
            if (chatType == GROUP_CHAT) {
                LogUtils.d("消息类型：群聊");
                message.setChatType(EMMessage.ChatType.GroupChat);
            } else if (chatType == CHAT_ROOM) {
                LogUtils.d("消息类型：聊天室");
                message.setChatType(EMMessage.ChatType.ChatRoom);
            }

            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
            // 如果会话是新的就获取一次
            if (conversation == null)
                conversation = EMClient.getInstance().chatManager().getConversation(target);

            getMessages();

            // 发送消息后自动滚动到最后一条
            LogUtils.d("消息数量：" + mMessages.size());
            rvMessages.smoothScrollToPosition(mMessages.size() - 1);
        }

    }

    private void getMessages() {
        // 如果会话是新的就没有会话对象，不获取消息
        if (conversation != null) {
            mMessages.clear();
            //获取此会话的所有消息
            List<EMMessage> messages = conversation.getAllMessages();
            mMessages.addAll(messages);
            messagesAdapter.notifyDataSetChanged();
            LogUtils.d("所有聊天记录条数：" + mMessages.size());
            // 收到消息后自动滚动到最后一条
            rvMessages.smoothScrollToPosition(mMessages.size() - 1);
        }

    }

    private void setToolbar() {
        if (chatType == GROUP_CHAT) {
            toolbar.setTitle(getIntent().getStringExtra("groupName"));
        } else {
            toolbar.setTitle(target);
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
