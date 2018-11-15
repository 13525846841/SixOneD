package com.yksj.healthtalk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleCustomerEntity implements Parcelable{
	
	private String userName;
	private String userSex;
	private String userId;
	private String userIconUrl;
	private String beChoose = "N";
	private String roleId;//客户角色 888医生
	

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getBeChoose() {
		return beChoose;
	}

	public void setBeChoose(String beChoose) {
		this.beChoose = beChoose;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIconUrl() {
		return userIconUrl;
	}

	public void setUserIconUrl(String userIconUrl) {
		this.userIconUrl = userIconUrl;
	}
	
	public void ChangeChoose() {
		if(beChoose.equals("N")) {
			beChoose = "Y";
		}else {
			beChoose = "N";	
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userIconUrl);
		dest.writeString(userId);
		dest.writeString(userName);
		dest.writeString(userSex);
		dest.writeString(beChoose);
		dest.writeString(roleId);
		
	}

	public static final Creator<SimpleCustomerEntity> CREATOR = new Creator<SimpleCustomerEntity>() {

		@Override
		public SimpleCustomerEntity createFromParcel(Parcel source) {
			SimpleCustomerEntity entity = new SimpleCustomerEntity();
			entity.setUserIconUrl(source.readString());
			entity.setUserId(source.readString());
			entity.setUserName(source.readString());
			entity.setUserSex(source.readString());
			entity.setBeChoose(source.readString());
			entity.setRoleId(source.readString());
			return entity;
		}

		@Override
		public SimpleCustomerEntity[] newArray(int size) {
			return null;
		}
	};
	
}
