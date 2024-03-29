package com.yksj.consultation.sonDoc.chatting.avchat.team;

import android.widget.Toast;

import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.viewholder.BaseAction;
import com.yksj.healthtalk.utils.NetworkUtil;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class AVChatAction extends BaseAction {
    private AVChatType avChatType;

    public AVChatAction(AVChatType avChatType) {
        super(avChatType == AVChatType.AUDIO ? R.drawable.message_plus_audio_chat_selector : R.drawable.message_plus_video_chat_selector,
                avChatType == AVChatType.AUDIO ? R.string.input_panel_audio_call : R.string.input_panel_video_call);
        this.avChatType = avChatType;
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            startAudioVideoCall(avChatType);
        } else {
            Toast.makeText(getActivity(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
//        AVChatActivity.launch(getActivity(), getAccount(), avChatType.getValue(), AVChatActivity.FROM_INTERNAL);
    }
}
