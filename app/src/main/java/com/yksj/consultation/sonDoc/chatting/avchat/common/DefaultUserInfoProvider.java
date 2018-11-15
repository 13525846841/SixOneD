package com.yksj.consultation.sonDoc.chatting.avchat.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.NimUserInfoCache;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.TeamDataCache;


/**
 * UIKit默认的用户信息提供者
 * <p>
 * Created by hzchenkang on 2016/12/19.
 */

public class DefaultUserInfoProvider implements UserInfoProvider {

    private Context context;

    public DefaultUserInfoProvider(Context context) {
        this.context = context;
    }

    @Override
    public UserInfo getUserInfo(String account) {
        UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
        if (user == null) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
        }

        return user;
    }
//
//    @Override
//    public int getDefaultIconResId() {
//        return R.drawable.default_head_doctor;
//    }

    @Override
    public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
        String nick = null;
        if (sessionType == SessionTypeEnum.P2P) {
            nick = NimUserInfoCache.getInstance().getAlias(account);
        } else if (sessionType == SessionTypeEnum.Team) {
            nick = TeamDataCache.getInstance().getDisplayNameWithoutMe(sessionId, account);
        }
        // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
        if (TextUtils.isEmpty(nick)) {
            return null;
        }

        return nick;
    }

    /*
     * 注意：这里最好从缓存里拿，如果加载时间过长会导致通知栏延迟弹出！该函数在后台线程执行！
     */
    @Override
    public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
        UserInfo user = getUserInfo(sessionId);
        return (user != null) ? NimUIKit.getImageLoaderKit().getNotificationBitmapFromCache(user.getAvatar()) : null;
    }

//    @Override
//    public Bitmap getTeamIcon(String teamId) {
//        /*
//         * 注意：这里最好从缓存里拿，如果加载时间过长会导致通知栏延迟弹出！该函数在后台线程执行！
//         */
//        Team team = TeamDataCache.getInstance().getTeamById(teamId);
//        if (team != null) {
//            Bitmap bm = NimUIKit.getImageLoaderKit().getNotificationBitmapFromCache(team.getIcon());
//            if (bm != null) {
//                return bm;
//            }
//        }
//
//        // 默认图
//        Drawable drawable = context.getResources().getDrawable(R.drawable.default_head_doctor);
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }
//
//        return null;
//    }
}
