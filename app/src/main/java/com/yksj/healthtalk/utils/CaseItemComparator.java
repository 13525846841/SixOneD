package com.yksj.healthtalk.utils;

import com.yksj.consultation.sonDoc.dossier.CaseItemEntity;

import java.util.Comparator;

/**
 * CaseItemEntity的比较器
 * Created by lmk on 2015/7/13.
 */
public class CaseItemComparator implements Comparator<CaseItemEntity>{
    @Override
    public int compare(CaseItemEntity lhs, CaseItemEntity rhs) {
        if(lhs.CLASSID>rhs.CLASSID){
            return 1;
        }else if(lhs.CLASSID<rhs.CLASSID){
            return -1;
        }else{
            if(lhs.SEQ>rhs.SEQ){
                return 1;
            }else if(lhs.SEQ<rhs.SEQ){
                return -1;
            }else{
                return 0;
            }
        }

    }
}
