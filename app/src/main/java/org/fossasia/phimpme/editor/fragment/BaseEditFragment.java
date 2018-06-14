package org.fossasia.phimpme.editor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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

     public abstract void onShow();
}
