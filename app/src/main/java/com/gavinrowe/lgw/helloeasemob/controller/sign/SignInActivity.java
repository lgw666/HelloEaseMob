package com.gavinrowe.lgw.helloeasemob.controller.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.controller.MainActivity;
import com.gavinrowe.lgw.helloeasemob.model.cache.CachePreferences;
import com.gavinrowe.lgw.helloeasemob.model.cache.UserInfo;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.gavinrowe.lgw.helloeasemob.utils.SimpleProgressDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 登陆界面
 */
public class SignInActivity extends AppCompatActivity {
    @BindView(R.id.et_un)
    EditText etUn;
    @BindView(R.id.et_pw)
    EditText etPw;
    private SimpleProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        progressDialog = new SimpleProgressDialog(this, "正在登陆...");
        // 将记录的用户名设置进去
        String un = CachePreferences.getUserInfo().getUn();
        if (un != null)
            etUn.setText(un);

    }

    // 加载本地会话和群组
    private void loadManagers() {
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    // 注册
    public void onSignUpClick(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    // 登陆
    public void onSignInClick(View view) {
        final String un = etUn.getText().toString();
        String pw = etPw.getText().toString();
        if (un.isEmpty()) {
            Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
        } else if (pw.isEmpty()) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            EMClient.getInstance().login(un, pw, new EMCallBack() {
                //回调
                @Override
                public void onSuccess() {
                    LogUtils.d("登陆成功");
                    progressDialog.dismiss();
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUn(un);
                    CachePreferences.setUserInfo(userInfo);
                    loadManagers();
                    finish();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(final int code, String message) {
                    LogUtils.e("登陆错误码：" + code);
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (EMError.INVALID_PASSWORD == code) {
                                Toast.makeText(SignInActivity.this, "登陆失败，密码不正确！", Toast.LENGTH_SHORT).show();
                            } else if (EMError.USER_AUTHENTICATION_FAILED == code) {
                                Toast.makeText(SignInActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                            } else if (EMError.USER_NOT_FOUND == code) {
                                Toast.makeText(SignInActivity.this, "登陆失败，用户不存在！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }
    }
}
