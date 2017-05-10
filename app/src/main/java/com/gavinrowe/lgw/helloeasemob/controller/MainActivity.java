package com.gavinrowe.lgw.helloeasemob.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.chat.fragment.ChatFragment;
import com.gavinrowe.lgw.helloeasemob.controller.settings.fragment.SettingsFragment;
import com.gavinrowe.lgw.helloeasemob.listener.ConnectionListener;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 会话管理
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment mChatFragment, mSettingsFragment;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mFragmentManager;
    // 当前Fragment位置
    private int mCurrentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();
        // 注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new ConnectionListener(this));
        loadManagers();
        mFragmentManager = getSupportFragmentManager();

        // 防止崩溃或者被系统回收后重启时点击崩溃
        if (savedInstanceState == null) {
            initFragments();
        } else {
            mChatFragment = mFragmentManager.findFragmentByTag(ChatFragment.class.getName());
            if (isNull(mChatFragment))
                mChatFragment = new ChatFragment();
            mFragments.add(mChatFragment);

            mSettingsFragment = mFragmentManager.findFragmentByTag(ChatFragment.class.getName());
            if (isNull(mSettingsFragment))
                mSettingsFragment = new SettingsFragment();
            mFragments.add(mSettingsFragment);

        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
    }

    // 加载本地会话和群组
    private void loadManagers() {
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    // 初始化Fragment
    private void initFragments() {
        mChatFragment = new ChatFragment();
        mSettingsFragment = new SettingsFragment();

        mFragments.add(mChatFragment);
        mFragments.add(mSettingsFragment);

        mFragmentManager
                .beginTransaction()
                .add(R.id.layout_fragments, mChatFragment, ChatFragment.class.getName())
                .commit();
    }

    // 选择Fragment
    private void switchFragment(int target) {
        if (target != mCurrentPos) {
            Fragment targetFragment = mFragments.get(target);// 目标Fragment
            Fragment currentFragment = mFragments.get(mCurrentPos);// 当前Fragment
            if (!targetFragment.isAdded())
                mFragmentManager.beginTransaction().add(R.id.layout_fragments, targetFragment, targetFragment.getClass().getName()).hide(currentFragment).commit();
            else
                mFragmentManager.beginTransaction().show(targetFragment).hide(currentFragment).commit();
            // 记录当前fragment位置
            mCurrentPos = target;
        }
    }

    // 会话Tab点击事件
    public void onChatClick(View view) {
        switchFragment(0);
    }

    // 设置Tab点击事件
    public void onSettingsClick(View view) {
        switchFragment(1);
    }

    // 判断Fragment是否未空
    private boolean isNull(Fragment f) {
        return f == null;
    }

}
