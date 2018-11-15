package com.yksj.healthtalk.entity;

/**
 * 通知消息a标签实体类
 * @author zhao
 */
public class ATagEntity {
	//<a type=\"customer\" id=\"24861\">1019527</a>
	private int type;//连接跳转类型群或者用户,1群2用户
	private String id;//连接id
	private String content;//内容
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
