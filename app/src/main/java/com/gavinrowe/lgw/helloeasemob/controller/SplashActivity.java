package com.gavinrowe.lgw.helloeasemob.controller;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.sign.SignInActivity;
import com.gavinrowe.lgw.helloeasemob.model.cache.CachePreferences;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (CachePreferences.getSignInStatus()) {
                    // 关闭当前页面并跳转到主界面
                    LogUtils.d("登陆状态：已登陆");
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    LogUtils.d("登陆状态：未登陆");
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                }
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }.sendMessageDelayed(Message.obtain(), 1500);
    }

}
