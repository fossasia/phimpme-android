package org.fossasia.phimpme.editor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.editor.EditImageActivity;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(MyApplication.isLeakCanaryInstalled){
            MyApplication.getRefWatcher(getActivity()).watch(this);
        }
    }

    public abstract void onShow();
}
