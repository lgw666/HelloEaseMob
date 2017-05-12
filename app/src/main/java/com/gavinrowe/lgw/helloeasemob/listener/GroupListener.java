package com.gavinrowe.lgw.helloeasemob.listener;

import android.content.Context;

import com.gavinrowe.lgw.helloeasemob.utils.LogUtils;
import com.hyphenate.EMGroupChangeListener;

import java.util.List;

/**
 * Author: Luo Guowen
 * Email: <a href="#">luoguowen123@qq.com</a>
 * Time: 2017/5/11
 * 群组监听
 */

public class GroupListener implements EMGroupChangeListener {

    public GroupListener(Context context) {

    }


    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        //收到加入群组的邀请
        LogUtils.d("群组监听 收到加入群组的邀请");
    }

    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
        //用户申请加入群
        LogUtils.d("群组监听 用户申请加入群");

    }

    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
        //加群申请被对方接受
        LogUtils.d("群组监听 加群申请被对方接受");

    }

    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
        //加群申请被拒绝
        LogUtils.d("群组监听 加群申请被拒绝");

    }

    @Override
    public void onInvitationAccepted(String groupId, String invitee, String reason) {
        //群组邀请被接受
        LogUtils.d("群组监听 群组邀请被接受");


    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {
        //群组邀请被拒绝
        LogUtils.d("群组监听 群组邀请被拒绝");

    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
        //当前用户被管理员移除出群组
        LogUtils.d("群组监听 当前用户被管理员移除出群组");

    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {
        //群组被创建者解散
        LogUtils.d("群组监听 群组被创建者解散");

    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
        //自动同意加入群组
        LogUtils.d("群组监听 自动同意加入群组");

    }

    @Override
    public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
        //成员被禁言，此处不同于blacklist
        LogUtils.d("群组监听 成员被禁言");

    }

    @Override
    public void onMuteListRemoved(String groupId, List<String> mutes) {
        //有成员从禁言列表中移除，恢复发言权限，此处不同于blacklist
        LogUtils.d("群组监听 有成员从禁言列表中移除，恢复发言权限");

    }

    @Override
    public void onAdminAdded(String groupId, String administrator) {
        // 添加成员管理员权限
        LogUtils.d("群组监听 添加成员管理员权限");

    }

    @Override
    public void onAdminRemoved(String groupId, String administrator) {
        // 取消某管理员权限
        LogUtils.d("群组监听 取消某管理员权限");

    }

    @Override
    public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
        // 转移群组所有者权限
        LogUtils.d("群组监听 转移群组所有者权限");

    }
}
