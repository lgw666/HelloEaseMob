package com.gavinrowe.lgw.helloeasemob.controller.chat.group.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.group.adapter.PublicGroupsAdapter;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// 公开的群组
public class PublicGroupsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_public_groups)
    RecyclerView rvPublicGroups;
    @BindView(R.id.layout_load_more)
    LinearLayout layoutLoadMore;

    private List<EMGroupInfo> mPublicGroups = new ArrayList<>();
    // 公开的群组的适配器
    private PublicGroupsAdapter publicGroupsAdapter;
    private String cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_groups);
        ButterKnife.bind(this);
        publicGroupsAdapter = new PublicGroupsAdapter(this, mPublicGroups);
        setToolbar();
        setRecyclerView();
        getPublicGroups();
    }

    // 获取公开的群组
    private void getPublicGroups() {
        //获取公开群列表
        //pageSize为要取到的群组的数量，cursor用于告诉服务器从哪里开始取，需异步处理
        new Thread() {
            @Override
            public void run() {
                try {
                    EMCursorResult<EMGroupInfo> result = EMClient.getInstance().groupManager().getPublicGroupsFromServer(10, cursor);
                    List<EMGroupInfo> publicGroups = result.getData();
                    LogUtils.d("群组数量publicGroups：" + publicGroups.size());
                    if (publicGroups.size() != 0) {
                        cursor = result.getCursor();
                        LogUtils.d("群组Cursor：" + cursor);
                        mPublicGroups.addAll(publicGroups);
                        LogUtils.d("群组数量mPublicGroups：" + mPublicGroups.size());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutLoadMore.setVisibility(View.GONE);
                                publicGroupsAdapter.notifyDataSetChanged();
                                // 滚动到最后一条
                                rvPublicGroups.smoothScrollToPosition(mPublicGroups.size() - 1);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutLoadMore.setVisibility(View.GONE);
                                Toast.makeText(PublicGroupsActivity.this, "没有数据了！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    // 标志 判断是否能加载了
    private boolean canLoad;
    private void setRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPublicGroups.setLayoutManager(layoutManager);
        rvPublicGroups.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPublicGroups.setAdapter(publicGroupsAdapter);
        rvPublicGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                canLoad = publicGroupsAdapter.getItemCount() - 1 == layoutManager.findLastVisibleItemPosition();
            }
        });
        // 上拉加载更多
        rvPublicGroups.setOnTouchListener(new View.OnTouchListener() {
            float startY = 0f, endY = 0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float y = startY - endY;
                        LogUtils.d("startY-endY=" + y);
                        if (canLoad && y > 25.0f) {
                            layoutLoadMore.setVisibility(View.VISIBLE);
                            getPublicGroups();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void setToolbar() {
        toolbar.setTitle("公开的群组");
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
