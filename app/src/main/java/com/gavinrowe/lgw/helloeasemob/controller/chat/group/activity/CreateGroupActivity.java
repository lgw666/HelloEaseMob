package com.gavinrowe.lgw.helloeasemob.controller.chat.group.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.gavinrowe.lgw.helloeasemob.R;
import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.gavinrowe.lgw.helloeasemob.utils.SimpleProgressDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

//创建群组
public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_group_name)
    EditText etGroupName;
    @BindView(R.id.et_group_desc)
    EditText etGroupDesc;
    @BindView(R.id.cb_gp_public)
    CheckBox cbGpPublic;
    @BindView(R.id.cb_gp_member_invitation)
    CheckBox cbGpMemberInvitation;
    @BindView(R.id.cb_gp_owner_agreement)
    CheckBox cbGpOwnerAgreement;

    private boolean isPublic, canMembersInvite, doNeedOwnerAgreement;

    private SimpleProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        progressDialog = new SimpleProgressDialog(this, "正在创建群组...");
        setToolbar();
        setCheckBox();

    }

    // 设置多多选框
    private void setCheckBox() {
        cbGpPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPublic = isChecked;
                if (isChecked) {
                    cbGpMemberInvitation.setVisibility(View.GONE);
                    cbGpOwnerAgreement.setVisibility(View.VISIBLE);
                } else {
                    cbGpMemberInvitation.setVisibility(View.VISIBLE);
                    cbGpOwnerAgreement.setVisibility(View.GONE);
                }
            }
        });
        cbGpMemberInvitation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                canMembersInvite = isChecked;
            }
        });
        cbGpOwnerAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doNeedOwnerAgreement = isChecked;
            }
        });
    }

    // 创建群组点击事件
    public void onCreateClick(View view) {
        String groupName = etGroupName.getText().toString();
        String decs = etGroupDesc.getText().toString();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "群组名称不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            if (isPublic) {
                if (doNeedOwnerAgreement) {
                    LogUtils.d("创建群组 类型：公开，群主允许加入");
                    createGroup(groupName, decs, EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval);
                } else {
                    LogUtils.d("创建群组 类型：公开，用户随意加入");
                    createGroup(groupName, decs, EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin);
                }
            } else {
                if (canMembersInvite) {
                    LogUtils.d("创建群组 类型：私有，允许成员邀请");
                    createGroup(groupName, decs, EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite);
                } else {
                    LogUtils.d("创建群组 类型：私有，允许群主邀请");
                    createGroup(groupName, decs, EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite);
                }
            }
        }

    }

    /**
     * 创建群组
     *
     * @param groupName       群组名称
     * @param desc            群组简介
     * @param groupPermission 群组权限
     *                        <p>
     *                        allMembers 群组初始成员，如果只有自己传空数组即可
     *                        reason     邀请成员加入的reason
     *                        <p>
     *                        option     群组类型选项@see {@link EMGroupManager.EMGroupStyle}
     *                        option.maxUsers = 200 最大用户数(默认200)
     *                        option.inviteNeedConfirm表示邀请对方进群是否需要对方同意，默认是需要用户同意才能加群的。
     *                        option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
     */
    private void createGroup(final String groupName, final String desc, final EMGroupManager.EMGroupStyle groupPermission) {
        progressDialog.show();
        final String reason = "";
        final String[] allMembers = {};
        LogUtils.d("创建群组信息："
                + "\ngroupName: " + groupName
                + "\ndesc: " + desc
                + "\nallMembers: " + Arrays.toString(allMembers)
                + "\nreason: " + reason);
        new Thread() {
            @Override
            public void run() {
                try {
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.style = groupPermission;
                    EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateGroupActivity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    LogUtils.e("创建群组错误：" + e.getErrorCode());
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }.start();


    }

    private void setToolbar() {
        toolbar.setTitle("新建群组");
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
