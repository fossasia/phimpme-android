package org.fossasia.phimpme.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.typeface.IIcon;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

import java.util.ArrayList;

/**
 * Created by dnld on 23/02/16.
 */
public class ThemedActivity extends BaseActivity {

    private ThemeHelper themeHelper;
    private PreferenceUtil SP;
    private boolean coloredNavBar;
    private boolean obscuredStatusBar;
    private boolean applyThemeImgAct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SP = PreferenceUtil.getInstance(getApplicationContext());
        themeHelper = new ThemeHelper(getApplicationContext());
        setNavBarColor();
        setNavigationBarColor(getPrimaryColor());
        setStatusBarColor();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTheme();
    }

    public void updateTheme() {
        themeHelper.updateTheme();
        coloredNavBar = SP.getBoolean(getString(R.string.preference_colored_nav_bar), true);
        obscuredStatusBar = SP.getBoolean(getString(R.string.preference_translucent_status_bar), true);
        applyThemeImgAct = SP.getBoolean(getString(R.string.preference_apply_theme_pager), true);
        setNavigationBarColor(getPrimaryColor());
        setNavBarColor();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // NOTE: icons stuff
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setNavBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isNavigationBarColored()) getWindow().setNavigationBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
            else
                getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTranslucentStatusBar())
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
            }
        }
    }

    protected void setScrollViewColor(ScrollView scr) {
        themeHelper.setScrollViewColor(scr);
    }

    public void setCursorDrawableColor(EditText editText, int color) {
        // TODO: 02/08/16 remove this
        ThemeHelper.setCursorDrawableColor(editText, color);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRecentApp(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(new ActivityManager.TaskDescription(text, getBitmapFromVectorDrawable(getApplicationContext(),R.drawable.ic_launcher_vector), getPrimaryColor()));
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public boolean isNavigationBarColored() {
        return coloredNavBar;
    }

    public boolean isTranslucentStatusBar() {
        return obscuredStatusBar;
    }

    protected boolean isApplyThemeOnImgAct() {
        return applyThemeImgAct;
    }

    protected boolean isTransparencyZero() {
        return 255 - SP.getInt(getString(R.string.preference_transparency), 0) == 255;
    }

    public int getTransparency() {
        return 255 - SP.getInt(getString(R.string.preference_transparency), 0);
    }

    public void setBaseTheme(int baseTheme, boolean permanent) {
        themeHelper.setBaseTheme(baseTheme, permanent);
    }

    public void themeSeekBar(SeekBar bar) {
        themeHelper.themeSeekBar(bar);
    }

    public int getPrimaryColor() {
        return themeHelper.getPrimaryColor();
    }

    public int getAccentColor() {
        return themeHelper.getAccentColor();
    }

    public int getBaseTheme() {
        return themeHelper.getBaseTheme();
    }

    protected int getBackgroundColor() {
        return themeHelper.getBackgroundColor();
    }

    protected Drawable getPlaceHolder() {
        return themeHelper.getPlaceHolder();
    }

    protected int getInvertedBackgroundColor() {
        return themeHelper.getInvertedBackgroundColor();
    }

    public int getTextColor() {
        return themeHelper.getTextColor();
    }

    public int getSubTextColor() {
        return themeHelper.getSubTextColor();
    }

    public int getCardBackgroundColor() {
        return themeHelper.getCardBackgroundColor();
    }

    public int getHighlightedItemColor() {
        return themeHelper.getHighlightedItemColor();
    }

    public int getIconColor() {
        return themeHelper.getIconColor();
    }

    protected int getDrawerBackground() {
        return themeHelper.getDrawerBackground();
    }

    public int getDialogStyle() {
        return themeHelper.getDialogStyle();
    }

    protected int getPopupToolbarStyle() {
        return themeHelper.getPopupToolbarStyle();
    }

    protected ArrayAdapter<String> getSpinnerAdapter(ArrayList<String> items) {
        return themeHelper.getSpinnerAdapter(items);
    }

    protected int getDefaultThemeToolbarColor3th() {
        return themeHelper.getDefaultThemeToolbarColor3th();
    }

    protected void updateRadioButtonColor(RadioButton radioButton) {
        themeHelper.updateRadioButtonColor(radioButton);
    }

    protected void setRadioTextButtonColor(RadioButton radioButton, int color) {
        themeHelper.setRadioTextButtonColor(radioButton, color);
    }

    public void updateSwitchColor(SwitchCompat sw, int color) {
        themeHelper.updateSwitchColor(sw, color);
    }

    public IconicsDrawable getToolbarIcon(IIcon icon) {
        return themeHelper.getToolbarIcon(icon);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_leafpic;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
}