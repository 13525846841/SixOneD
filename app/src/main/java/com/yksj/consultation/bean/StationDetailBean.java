package com.yksj.consultation.bean;

import java.util.List;

/**
 * 工作站详情
 */
public class StationDetailBean {
    public List<DoctorServiceBean> siteService;
    public StationHeadBean siteInfo;
    public List<StationMemberBean> siteMember;
    public StationCommentBean siteeValuate;
    public String qrCodeUrl;
}
