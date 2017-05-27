package com.xinlan.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

/**
 * Created by panyi on 2017/3/28.
 */

public abstract class BaseEditFragment extends Fragment {
    protected EditImageActivity activity;

    protected EditImageActivity ensureEditActivity(){
        if(activity==null){
            activity = (EditImageActivity)getActivity();
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureEditActivity();
    }

     public abstract void onShow();
}//end class
