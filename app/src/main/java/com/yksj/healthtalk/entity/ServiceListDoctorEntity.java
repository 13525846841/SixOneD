package com.yksj.healthtalk.entity;


public class ServiceListDoctorEntity {
	private int consultationId;// 会诊id
	private int consultationCenterId;// 六一健康id
	private String centerName;// 疾病名称
	private String centerTime;// 会诊时间
	private String consultationStatus;// 会诊状态
	private int patientId;// 患者id
	private String bigIconBackground;// 大头像
	private String clientIconBackground;// 小头像
	private String customerNickName;// 昵称
	private int serviceStatusName;// 列表项前标记
	private String serviceOperation;// 列表项后标记
	public int getConsultationId() {
		return consultationId;
	}
	public void setConsultationId(int consultationId) {
		this.consultationId = consultationId;
	}
	public int getConsultationCenterId() {
		return consultationCenterId;
	}
	public void setConsultationCenterId(int consultationCenterId) {
		this.consultationCenterId = consultationCenterId;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCenterTime() {
		return centerTime;
	}
	public void setCenterTime(String centerTime) {
		this.centerTime = centerTime;
	}
	public String getConsultationStatus() {
		return consultationStatus;
	}
	public void setConsultationStatus(String consultationStatus) {
		this.consultationStatus = consultationStatus;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public String getBigIconBackground() {
		return bigIconBackground;
	}
	public void setBigIconBackground(String bigIconBackground) {
		this.bigIconBackground = bigIconBackground;
	}
	public String getClientIconBackground() {
		return clientIconBackground;
	}
	public void setClientIconBackground(String clientIconBackground) {
		this.clientIconBackground = clientIconBackground;
	}
	public String getCustomerNickName() {
		return customerNickName;
	}
	public void setCustomerNickName(String customerNickName) {
		this.customerNickName = customerNickName;
	}
	public int getServiceStatusName() {
		return serviceStatusName;
	}
	public void setServiceStatusName(int serviceStatusName) {
		this.serviceStatusName = serviceStatusName;
	}
	public String getServiceOperation() {
		return serviceOperation;
	}
	public void setServiceOperation(String serviceOperation) {
		this.serviceOperation = serviceOperation;
	}
	@Override
	public String toString() {
		return "ServiceListDoctorEntity [consultationId=" + consultationId + ", consultationCenterId="
				+ consultationCenterId + ", centerName=" + centerName + ", centerTime=" + centerTime
				+ ", consultationStatus=" + consultationStatus + ", patientId=" + patientId + ", bigIconBackground="
				+ bigIconBackground + ", clientIconBackground=" + clientIconBackground + ", customerNickName="
				+ customerNickName + ", serviceStatusName=" + serviceStatusName + ", serviceOperation="
				+ serviceOperation + "]";
	}
	
}
