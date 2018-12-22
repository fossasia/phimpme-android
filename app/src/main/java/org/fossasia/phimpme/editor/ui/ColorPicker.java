package org.fossasia.phimpme.editor.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import org.fossasia.phimpme.R;

/**
 * 颜色选择器
 * Created by panyi on 2016/6/9.
 */
public class ColorPicker extends Dialog implements SeekBar.OnSeekBarChangeListener {
    private static final String COLOR_STRING_FORMAT = "#%02x%02x%02x";

    public Activity c;
    public Dialog d;

    View colorView;
    SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    TextView redToolTip, greenToolTip, blueToolTip;
    EditText codHex;
    private int red, green, blue;
    int seekBarLeft;
    Rect thumbRect;

    public ColorPicker(Activity a) {
        super(a);

        this.c = a;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }


    public ColorPicker(Activity a, int r, int g, int b) {
        super(a);

        this.c = a;

        if (0 <= r && r <= 255)
            this.red = r;
        else
            this.red = 0;

        if (0 <= r && r <= 255)
            this.green = g;
        else
            this.green = 0;

        if (0 <= r && r <= 255)
            this.blue = b;
        else
            this.green = 0;
    }


    /**
     * Simple onCreate function. Here there is the init of the GUI.
     *
     * @param savedInstanceState As usual ...
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.materialcolorpicker__layout_color_picker);
        } else {
            setContentView(R.layout.materialcolorpicker__layout_color_picker_old_android);
        }

        colorView = findViewById(R.id.colorView);

        redSeekBar = (SeekBar) findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar) findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar) findViewById(R.id.blueSeekBar);

        seekBarLeft = redSeekBar.getPaddingLeft();

        redToolTip = (TextView) findViewById(R.id.redToolTip);
        greenToolTip = (TextView) findViewById(R.id.greenToolTip);
        blueToolTip = (TextView) findViewById(R.id.blueToolTip);

        codHex = (EditText) findViewById(R.id.codHex);

        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);

        colorView.setBackgroundColor(Color.rgb(red, green, blue));

        codHex.setText(String.format(COLOR_STRING_FORMAT, red, green, blue));
        codHex.setEnabled(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        thumbRect = redSeekBar.getThumb().getBounds();

        redToolTip.setX(seekBarLeft + thumbRect.left);
        if (red < 10)
            redToolTip.setText("  " + red);
        else if (red < 100)
            redToolTip.setText(" " + red);
        else
            redToolTip.setText(red + "");

        thumbRect = greenSeekBar.getThumb().getBounds();

        greenToolTip.setX(seekBarLeft + thumbRect.left);
        if (green < 10)
            greenToolTip.setText("  " + green);
        else if (red < 100)
            greenToolTip.setText(" " + green);
        else
            greenToolTip.setText(green + "");

        thumbRect = blueSeekBar.getThumb().getBounds();

        blueToolTip.setX(seekBarLeft + thumbRect.left);
        if (blue < 10)
            blueToolTip.setText("  " + blue);
        else if (blue < 100)
            blueToolTip.setText(" " + blue);
        else
            blueToolTip.setText(blue + "");

    }

    /**
     * Method called when the user change the value of the bars. This sync the colors.
     *
     * @param seekBar  SeekBar that has changed
     * @param progress The new progress value
     * @param fromUser If it coem from User
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar.getId() == R.id.redSeekBar) {

            red = progress;
            thumbRect = seekBar.getThumb().getBounds();

            redToolTip.setX(seekBarLeft + thumbRect.left);

            if (progress < 10)
                redToolTip.setText("  " + red);
            else if (progress < 100)
                redToolTip.setText(" " + red);
            else
                redToolTip.setText(red + "");

        } else if (seekBar.getId() == R.id.greenSeekBar) {

            green = progress;
            thumbRect = seekBar.getThumb().getBounds();

            greenToolTip.setX(seekBar.getPaddingLeft() + thumbRect.left);
            if (progress < 10)
                greenToolTip.setText("  " + green);
            else if (progress < 100)
                greenToolTip.setText(" " + green);
            else
                greenToolTip.setText(green + "");

        } else if (seekBar.getId() == R.id.blueSeekBar) {

            blue = progress;
            thumbRect = seekBar.getThumb().getBounds();

            blueToolTip.setX(seekBarLeft + thumbRect.left);
            if (progress < 10)
                blueToolTip.setText("  " + blue);
            else if (progress < 100)
                blueToolTip.setText(" " + blue);
            else
                blueToolTip.setText(blue + "");

        }

        colorView.setBackgroundColor(Color.rgb(red, green, blue));

        //Setting the inputText hex color
        codHex.setText(String.format(COLOR_STRING_FORMAT, red, green, blue));

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getColor() {
        return Color.rgb(red, green, blue);
    }
}