package com.gavinrowe.lgw.helloeasemob.controller.sign;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.gavinrowe.lgw.helloeasemob.utils.SimpleProgressDialog;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 注册
 */
public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.et_confirm_pw)
    EditText etConfirmPw;
    @BindView(R.id.et_un)
    EditText etUn;
    @BindView(R.id.et_pw)
    EditText etPw;

    private SimpleProgressDialog progressDialog;
    private Handler signUpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            progressDialog.dismiss();
            if (code == 0) {
                Toast.makeText(SignUpActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "注册失败，用户名已存在！", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        progressDialog = new SimpleProgressDialog(this, "正在注册");
    }


    public void onConfirmSignUpClick(View view) {
        final String un = etUn.getText().toString();
        final String pw = etPw.getText().toString();
        String confirmPw = etConfirmPw.getText().toString();
        if (un.isEmpty()) {
            Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
        } else if (pw.isEmpty()) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
        } else if (confirmPw.isEmpty()) {
            Toast.makeText(this, "请再次输入密码！！", Toast.LENGTH_SHORT).show();
        } else if (!pw.equals(confirmPw)) {
            Toast.makeText(this, "两次输入的密码不同！", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            //注册失败会抛出HyphenateException
            LogUtils.d("注册用户信息：\n用户名：" + un + "\n密码：" + pw + "\n确认密码：" + confirmPw);
            new Thread() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(un, pw);//同步方法
                        signUpHandler.sendEmptyMessage(0);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        if (EMError.USER_ALREADY_EXIST == e.getErrorCode())
                            signUpHandler.sendEmptyMessage(1);
                    }
                }
            }.start();
        }

    }
}
