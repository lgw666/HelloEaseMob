package com.gavinrowe.lgw.helloeasemob.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/8
 * 进度对话框
 */

public class SimpleProgressDialog extends ProgressDialog {

    /**
     * @param context 上下文对象
     * @param msg     信息
     */
    public SimpleProgressDialog(Context context, String msg) {
        super(context);
        setMessage(msg);
        setCanceledOnTouchOutside(false);
    }

    private void setMessage(String msg) {
        super.setMessage(msg);
    }

}
