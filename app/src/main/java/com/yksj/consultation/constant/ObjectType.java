package com.yksj.consultation.constant;

/**
 * Created by hww on 17/4/27.
 * Used for
 */

public class ObjectType {
    public final static String CONSULT = "10";
    public final static String OUTPATIENT = "20";
    public final static String SPECIAL_SERVER = "30";
    public final static String TUWEN = "40";
    public final static String STATION_CHAT = "50";

    public static boolean isStationChat(String type){
        return STATION_CHAT.equals(type);
    }
}
