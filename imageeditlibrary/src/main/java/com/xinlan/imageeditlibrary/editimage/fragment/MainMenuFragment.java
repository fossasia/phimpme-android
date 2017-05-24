package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 工具栏主菜单
 *
 * @author panyi
 */
public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = 0;

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;

    private View stickerBtn;// 贴图按钮
    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    private View rotateBtn;// 旋转按钮
    private View mTextBtn;//文字型贴图添加
    private View mPaintBtn;//编辑按钮

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        fliterBtn = mainView.findViewById(R.id.btn_fliter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);

        stickerBtn.setOnClickListener(this);
        fliterBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mPaintBtn.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void onClick(View v) {
        if (v == stickerBtn) {
            onStickClick();
        } else if (v == fliterBtn) {
            onFilterClick();
        } else if (v == cropBtn) {
            onCropClick();
        } else if (v == rotateBtn) {
            onRotateClick();
        } else if (v == mTextBtn) {
            onAddTextClick();
        } else if (v == mPaintBtn) {
            onPaintClick();
        }
    }

    /**
     * 贴图模式
     *
     * @author panyi
     */
    private void onStickClick() {
        activity.bottomGallery.setCurrentItem(StirckerFragment.INDEX);
        activity.mStirckerFragment.onShow();
    }

    /**
     * 滤镜模式
     *
     * @author panyi
     */
    private void onFilterClick() {
        activity.bottomGallery.setCurrentItem(FliterListFragment.INDEX);

        activity.mFliterListFragment.onShow();
    }

    /**
     * 裁剪模式
     *
     * @author panyi
     */
    private void onCropClick() {
        activity.bottomGallery.setCurrentItem(CropFragment.INDEX);
        activity.mCropFragment.onShow();
    }

    /**
     * 图片旋转模式
     *
     * @author panyi
     */
    private void onRotateClick() {
        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
        activity.mRotateFragment.onShow();
    }

    /**
     * 插入文字模式
     *
     * @author panyi
     */
    private void onAddTextClick() {
        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.mAddTextFragment.onShow();
    }

    /**
     * 自由绘制模式
     */
    private void onPaintClick() {
        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.mPaintFragment.onShow();
    }

}// end class
