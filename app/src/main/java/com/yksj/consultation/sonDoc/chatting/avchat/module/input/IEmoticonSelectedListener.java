package com.yksj.consultation.sonDoc.chatting.avchat.module.input;

public interface IEmoticonSelectedListener {
	void onEmojiSelected(String key);

	void onStickerSelected(String categoryName, String stickerName);
}
