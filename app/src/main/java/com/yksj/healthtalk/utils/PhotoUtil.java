package com.yksj.healthtalk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by ${chen} on 2017/2/24.
 */
public class PhotoUtil {
    /**
     * 生成一个随机的文件名
     * @return
     */
    public static String getRandomFileName() {
        String rel="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel+new Random().nextInt(1000);
        return rel;
    }
}
