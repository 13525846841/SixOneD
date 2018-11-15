package com.yksj.healthtalk.entity;
/** 
* @ClassName: SalonAndSocietyEntity 
* @Description: 沙龙和社交场
* @author wangtao wt0710910108_gmail_com  
* @date 2012-12-30 上午10:45:22  
*/ 
public class SalonAndSocietyEntity {
	private  String headImageUrl;//头像路劲地址
	private  String name;//名称 
	private  String signature;//签名
	
	public SalonAndSocietyEntity() {
		super();
	}
	public SalonAndSocietyEntity(String headImageUrl, String name,
			String signature) {
		super();
		this.headImageUrl = headImageUrl;
		this.name = name;
		this.signature = signature;
	}
	public String getHeadImageUrl() {
		return headImageUrl;
	}
	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}
