package com.yksj.consultation.bean;

import com.yksj.consultation.app.AppContext;

import java.util.ArrayList;
import java.util.List;

public class MainBannerBean {
    public List<BannerInfo> info;

    public static class BannerInfo{
        public String ANDROID_BANNER_1X;
        public String ANDROID_BANNER_2X;
        public String ANDROID_BANNER_3X;
        public int BANNER_ID;
        public String BANNER_DESC;
        public int BANNER_SEQ;
        public int BANNER_TYPE;
        public String PARAMETERS;
        public String PAGE_TITLE;
    }

    @Override
    public String toString() {
        return "MainBannerBean{" +
                "info=" + info +
                '}';
    }

    public List getImages(){
        List images = new ArrayList();
        for (BannerInfo info : info) {
            String imagePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + info.ANDROID_BANNER_2X;
            images.add(imagePath);
        }
        return images;
    }

    public List<String> getTitle(){
        List<String> titles = new ArrayList<>();
        for (BannerInfo info : info) {
            titles.add(info.BANNER_DESC);
        }
        return titles;
    }
}
