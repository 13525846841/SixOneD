package com.yksj.healthtalk.entity;

import java.io.Serializable;
/**
 * 留言实体
 * @author Administrator
 *
 */
public class LeaveMessage implements Serializable{

	private String CLIENT_ICON_BACKGROUND;
	private String BIG_ICON_BACKGROUND;
	private String MESSAGE_CONTENT;
	private String CUSTOMER_ID;
	private String MESSAGE_TIME;
	private String MESSAGE_ID;
	private String MESSAGE_TYPE;
	private String RNUM;
	private int check=1;
	private String REPLY_NICKNAME;
	private String CUSTOMER_NICKNAME;
	private String isDoctor;
	private String CUSTOMER_SEX;
	
	public String getCUSTOMER_SEX() {
		return CUSTOMER_SEX;
	}
	public void setCUSTOMER_SEX(String cUSTOMER_SEX) {
		CUSTOMER_SEX = cUSTOMER_SEX;
	}
	public String isDoctor() {
		return isDoctor;
	}
	public void setDoctor(String isDoctor) {
		this.isDoctor = isDoctor;
	}
	public String getCUSTOMER_NICKNAME() {
		return CUSTOMER_NICKNAME;
	}
	public void setCUSTOMER_NICKNAME(String cUSTOMER_NICKNAME) {
		CUSTOMER_NICKNAME = cUSTOMER_NICKNAME;
	}
	public String getREPLY_NICKNAME() {
		return REPLY_NICKNAME;
	}
	public void setREPLY_NICKNAME(String rEPLY_NICKNAME) {
		REPLY_NICKNAME = rEPLY_NICKNAME;
	}
	public int isCheck() {
		return check;
	}
	public void setCheck(int check) {
		this.check = check;
	}
	public String getCLIENT_ICON_BACKGROUND() {
		return CLIENT_ICON_BACKGROUND;
	}
	public void setCLIENT_ICON_BACKGROUND(String cLIENT_ICON_BACKGROUND) {
		CLIENT_ICON_BACKGROUND = cLIENT_ICON_BACKGROUND;
	}
	public String getBIG_ICON_BACKGROUND() {
		return BIG_ICON_BACKGROUND;
	}
	public void setBIG_ICON_BACKGROUND(String bIG_ICON_BACKGROUND) {
		BIG_ICON_BACKGROUND = bIG_ICON_BACKGROUND;
	}
	public String getMESSAGE_CONTENT() {
		return MESSAGE_CONTENT;
	}
	public void setMESSAGE_CONTENT(String mESSAGE_CONTENT) {
		MESSAGE_CONTENT = mESSAGE_CONTENT;
	}
	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}
	public void setCUSTOMER_ID(String cUSTOMER_ID) {
		CUSTOMER_ID = cUSTOMER_ID;
	}
	public String getMESSAGE_TIME() {
		return MESSAGE_TIME;
	}
	public void setMESSAGE_TIME(String mESSAGE_TIME) {
		MESSAGE_TIME = mESSAGE_TIME;
	}
	public String getMESSAGE_ID() {
		return MESSAGE_ID;
	}
	public void setMESSAGE_ID(String mESSAGE_ID) {
		MESSAGE_ID = mESSAGE_ID;
	}
	public String getMESSAGE_TYPE() {
		return MESSAGE_TYPE;
	}
	public void setMESSAGE_TYPE(String mESSAGE_TYPE) {
		MESSAGE_TYPE = mESSAGE_TYPE;
	}
	public String getRNUM() {
		return RNUM;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
}
