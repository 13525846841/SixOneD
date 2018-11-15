package com.yksj.healthtalk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class TicketChargeEntity {
	int charge;
	int defineType; //1是选项  2是 自定义
	int ticketType;//1是日票 2是月票
	
	public TicketChargeEntity(int charge, int defineType, int ticketType) {
		super();
		this.charge = charge;
		this.defineType = defineType;
		this.ticketType = ticketType;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public int getDefineType() {
		return defineType;
	}
	public void setDefineType(int defineType) {
		this.defineType = defineType;
	}
	public int getTicketType() {
		return ticketType;
	}
	public void setTicketType(int ticketType) {
		this.ticketType = ticketType;
	}
	@Override
	public String toString() {
		return "{charge=" + charge + ", defineType="
				+ defineType + ", ticketType=" + ticketType + "}";
	}
//	this.charge = charge;
//	this.defineType = defineType;
//	this.ticketType = ticketType;
	public JSONObject getJsonObject(){
		JSONObject  jsonObject = new JSONObject();
		try {
			jsonObject.put("charge", this.charge);
			jsonObject.put("defineType", this.defineType);
			jsonObject.put("ticketType", this.ticketType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	
}
