package com.yksj.consultation.sonDoc.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import com.blankj.utilcode.util.FragmentUtils;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.notify.ChatNotification;
import com.yksj.consultation.sonDoc.R;

/**
 * 主页 底部  我的消息  患者端
 *
 * @author jack_tang
 */
public class MessageNotifyActivity extends BaseTitleActivity implements OnClickListener {

    @Override
    public int createLayoutRes() {
        return R.layout.message_notify_activity_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("我的消息");
        ChatNotification.getInstance().clearUnread();
        if (FragmentUtils.findFragment(getSupportFragmentManager(), MessageHistoryFragment.class) == null) {
            Fragment fragment = MessageHistoryFragment.newInstance();
            FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.fragment);
        }
    }
}
