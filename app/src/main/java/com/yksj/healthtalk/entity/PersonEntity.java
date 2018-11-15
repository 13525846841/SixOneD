package com.yksj.healthtalk.entity;

/**
 * 为了显示按拼音排序的用户,这里建一个用户entity,用户信息并没有那么多
 * Created by lmk on 2015/7/8.
 *
 * customerId   		客户id
 infoVersion  		资料版本号
 customerLocus		地区编码
 dwellingPlace		地区名称
 customerAccounts	账号
 customerNickname	昵称
 roleId			角色id
 customerSex		性别
 personalNarrate		个性签名
 bigIconBackground	大头像
 clientIconBackground  	小头像
 remarksName		备注名
 patientGroupID		患者分组id
 relationType		客户关系标记  0-无关系  1-我的病友  2-黑名单 3-我的客户 4-我的医生
 */
public class PersonEntity {
   private String customerNickname;//名称
   private String bigIconBackground;//头像
   private String customerAccounts;//账号
   private String customerId;//id
   private int customerSex;//性别

   private String sortLetters;  //显示数据拼音的首字母

 public String getCustomerNickname() {
  return customerNickname;
 }

 public void setCustomerNickname(String customerNickname) {
  this.customerNickname = customerNickname;
 }

 public String getCustomerAccounts() {
  return customerAccounts;
 }

 public void setCustomerAccounts(String customerAccounts) {
  this.customerAccounts = customerAccounts;
 }

 public String getBigIconBackground() {
  return bigIconBackground;
 }

 public void setBigIconBackground(String bigIconBackground) {
  this.bigIconBackground = bigIconBackground;
 }

 public String getCustomerId() {
  return customerId;
 }

 public void setCustomerId(String customerId) {
  this.customerId = customerId;
 }

 public int getCustomerSex() {
  return customerSex;
 }

 public void setCustomerSex(int customerSex) {
  this.customerSex = customerSex;
 }

 public String getSortLetters() {
  return sortLetters;
 }

 public void setSortLetters(String sortLetters) {
  this.sortLetters = sortLetters;
 }
}
