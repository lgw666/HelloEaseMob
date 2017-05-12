package com.gavinrowe.lgw.helloeasemob.controller.chat.group.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.gavinrowe.lgw.helloeasemob.utils.SimpleProgressDialog;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;

// 加入群聊
public class JoinGroupActivity extends AppCompatActivity {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_owner)
    TextView tvOwner;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String groupId;

    private SimpleProgressDialog progressDialog;
    private EMGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setToolbar();
        progressDialog = new SimpleProgressDialog(this, "正在申请加入群组...");
        groupId = getIntent().getStringExtra("groupId");
        // 让简介可滚动
        tvDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        getGroupInfo();

    }

    // 获取群组信息
    private void getGroupInfo() {
        //根据群组ID从服务器获取群组基本信息
        new Thread() {
            @Override
            public void run() {
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 名字
                            tvName.setText(group.getGroupName());

                            //群主
                            String owner = "群主:" + group.getOwner();
                            tvOwner.setText(owner);

                            // 简介
                            String desc = "简介：" + group.getDescription();
                            tvDesc.setText(desc);

                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    // 加入群组点击事件
    public void onJoinClick(View view) {
        progressDialog.show();
        //需要申请和验证才能加入的，即group.isMembersOnly()为true，需异步处理
        if (group.isMemberOnly()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().applyJoinToGroup(groupId, "请求加入");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(JoinGroupActivity.this, "申请成功，等待群主审核！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } catch (HyphenateException e) {
                        final int code = e.getErrorCode();
                        e.printStackTrace();
                        LogUtils.e("加入群组错误码：" + code);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (code == EMError.GROUP_ALREADY_JOINED) {
                                    Toast.makeText(JoinGroupActivity.this, "已经加入过了！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }.start();
        } else {
            //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join，需异步处理
            new Thread() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().joinGroup(groupId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(JoinGroupActivity.this, "加入成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } catch (HyphenateException e) {
                        final int code = e.getErrorCode();
                        e.printStackTrace();
                        LogUtils.e("加入群组错误码：" + code);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (code == EMError.GROUP_ALREADY_JOINED) {
                                    Toast.makeText(JoinGroupActivity.this, "已经加入过了！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }.start();
        }


    }

    private void setToolbar() {
        toolbar.setTitle("加入群组");
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
