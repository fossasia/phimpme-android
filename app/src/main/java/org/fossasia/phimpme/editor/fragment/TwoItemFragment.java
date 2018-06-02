package org.fossasia.phimpme.editor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;

public class TwoItemFragment extends BaseEditFragment implements View.OnClickListener{
    LinearLayout ll_item1, ll_item2;
    ImageView icon1,icon2;
    TextView text1,text2;
    int mode;
    public TwoItemFragment() {

    }

    public static TwoItemFragment newInstance(int mode) {
        TwoItemFragment fragment = new TwoItemFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor_twoitem, container, false);


        ll_item1 = (LinearLayout)view.findViewById(R.id.menu_item1);
        ll_item2 = (LinearLayout)view.findViewById(R.id.menu_item2);

        icon1 = (ImageView) view.findViewById(R.id.item1iconimage);
        icon2 = (ImageView) view.findViewById(R.id.item2iconimage);

        text1 = (TextView) view.findViewById(R.id.item1text);
        text2 = (TextView) view.findViewById(R.id.item2text);

        ll_item1.setOnClickListener(this);
        ll_item2.setOnClickListener(this);

        icon1.setImageResource(R.drawable.ic_text);
        icon2.setImageResource(R.drawable.ic_paint);

        text1.setText(getString(R.string.text));
        text2.setText(getString(R.string.paint));


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_item1:
                clearSelection();
                ll_item1.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.md_grey_200));
                firstItemClicked();
                break;
            case R.id.menu_item2:
                clearSelection();
                ll_item2.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.md_grey_200));
                secondItemClicked();
                break;
        }
    }

    public void clearSelection() {
        ll_item1.setBackgroundColor(Color.TRANSPARENT);
        ll_item2.setBackgroundColor(Color.TRANSPARENT);
    }


    private void firstItemClicked() {
        activity.changeMode(EditImageActivity.MODE_TEXT);
        activity.changeBottomFragment(EditImageActivity.MODE_TEXT);
    }

    private void secondItemClicked() {
        activity.changeMode(EditImageActivity.MODE_PAINT);
        activity.changeBottomFragment(EditImageActivity.MODE_PAINT);
    }

}
