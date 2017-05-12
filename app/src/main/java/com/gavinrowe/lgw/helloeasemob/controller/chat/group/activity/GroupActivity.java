package com.gavinrowe.lgw.helloeasemob.controller.chat.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.group.adapter.MyGroupsAdapter;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// 群组
public class GroupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_groups)
    RecyclerView rvGroups;

    private List<EMGroup> mMyGroups = new ArrayList<>();
    // 群组适配器
    private MyGroupsAdapter myGroupsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        myGroupsAdapter = new MyGroupsAdapter(this, mMyGroups);
        setToolbar();
        setRecyclerView();
    }

    private void setRecyclerView() {
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(myGroupsAdapter);
        rvGroups.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMyGroups();
    }

    // 获取自己加入和创建的群组
    private void getMyGroups() {
        //从服务器获取自己加入的和创建的群组列表，此api获取的群组sdk会自动保存到内存和db。
        new Thread() {
            @Override
            public void run() {
                try {
                    mMyGroups.clear();
                    List<EMGroup> myGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    LogUtils.d("群组数量：" + myGroups.size());
                    mMyGroups.addAll(myGroups);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myGroupsAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setToolbar() {
        toolbar.setTitle("群组");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_create_group:
                startActivity(new Intent(this, CreateGroupActivity.class));
                break;
            case R.id.menu_join_group:
                startActivity(new Intent(this, PublicGroupsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
