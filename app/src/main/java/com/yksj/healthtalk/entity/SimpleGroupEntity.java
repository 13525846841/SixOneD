package com.yksj.healthtalk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleGroupEntity implements Parcelable{
	
	/**
	 * 头像地址
	 */
	private String iconUrl;
	/**
	 * 沙龙类型  1- 医师        2-普通
	 */
	private String groupClass;
	/**
	 * 沙龙id
	 */
	private String groupId;
	/**
	 * 沙龙等级
	 */
	private String groupLevel;
	/**
	 * 沙龙名称
	 */
	private String recordName;
	/**
	 * Y or N
	 */
	private String beChoose = "N";

	public String getBeChoose() {
		return beChoose;
	}

	public void setBeChoose(String beChoose) {
		this.beChoose = beChoose;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getGroupClass() {
		return groupClass;
	}

	public void setGroupClass(String groupClass) {
		this.groupClass = groupClass;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
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
		dest.writeString(groupClass);
		dest.writeString(this.groupId);
		dest.writeString(this.groupLevel);
		dest.writeString(this.iconUrl);
		dest.writeString(this.recordName);
		dest.writeString(beChoose);
	}
	
	public static final Creator<SimpleGroupEntity> CREATOR = new Creator<SimpleGroupEntity>() {

		@Override
		public SimpleGroupEntity createFromParcel(Parcel source) {
			SimpleGroupEntity entity = new SimpleGroupEntity();
			entity.setGroupClass(source.readString());
			entity.setGroupId(source.readString());
			entity.setGroupLevel(source.readString());
			entity.setIconUrl(source.readString());
			entity.setRecordName(source.readString());
			entity.setBeChoose(source.readString());
			return entity;
		}

		@Override
		public SimpleGroupEntity[] newArray(int size) {
			return null;
		}
	};
	
}
