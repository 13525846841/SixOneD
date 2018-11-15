package com.library.base.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.reactivex.subjects.PublishSubject;

public class RxChooseResultFragment extends Fragment {

    static final int CHOOSE_REQUEST = 909;
    private PublishSubject mSubject;

    public static RxChooseResultFragment newInstance() {

        Bundle args = new Bundle();

        RxChooseResultFragment fragment = new RxChooseResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void bindSubject(PublishSubject subject){
        mSubject = subject;
    }

    public PublishSubject getSubject(){
        return mSubject;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case CHOOSE_REQUEST:
                    mSubject.onNext(data);
                    mSubject.onComplete();
            }
        }
    }
}
