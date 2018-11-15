package com.yksj.consultation.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.message.MessageNotifyActivity;

/**
 * IM消息通知
 */
public class ChatNotification {

    private static final int CALLING_NOTIFICATION_REQUEST = 0x00001;
    private final NotificationManager mNotification;
    private Notification mCallingNotification;
    private Context mContext;
    private int mUnreadNum;
    private static ChatNotification mInstance;

    public static ChatNotification getInstance(){
        if (mInstance == null) {
            synchronized (ChatNotification.class){
                if (mInstance == null){
                    mInstance = new ChatNotification(Utils.getApp());
                }
            }
        }
        return mInstance;
    }

    public NotificationManager getNotification() {
        return mNotification;
    }

    private ChatNotification(Context context) {
        this.mContext = Utils.getApp();
        mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ChatNotificationChannelCompat26.createNIMMessageNotificationChannel(context);
    }

    public void clearUnread(){
        mUnreadNum = 0;
    }

    public void activeNotification(boolean active) {
        if (showNotification()) {
            if (mNotification != null) {
                if (active) {
                    mUnreadNum++;
                    String title = "六一健康";
                    String content = String.format("未读消息%s条", mUnreadNum);
                    buildCallingNotification(title, content);
                    mNotification.notify(CALLING_NOTIFICATION_REQUEST, mCallingNotification);
                } else {
                    mNotification.cancel(CALLING_NOTIFICATION_REQUEST);
                }
            }
        }
    }

    private boolean showNotification(){
        return !(ActivityUtils.getTopActivity() instanceof MessageNotifyActivity);
    }

    private void buildCallingNotification(String title, String content) {
        mCallingNotification = new Notification();
        Intent intent = new Intent();
        intent.setClass(mContext, MessageNotifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, CALLING_NOTIFICATION_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mCallingNotification = makeNotification(pendingIntent, title, content, R.drawable.ic_launcher, true, true);
    }

    private Notification makeNotification(PendingIntent pendingIntent, String title, String content, int iconId, boolean sound, boolean vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, ChatNotificationChannelCompat26.getNIMChannelId(mContext));
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(iconId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        builder.setDefaults(defaults);
        return builder.build();
    }
}
