package org.fossasia.phimpme.editor.font;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.fossasia.phimpme.R;

/** This class gives a dialog for selecting a font */
public class FontPickerDialog extends DialogFragment {

  private List<String> mFontPaths; // list of file paths for the available fonts
  private List<String> mFontNames; // font names of the available fonts
  private String mSelectedFont;
  private FontPickerDialogListener mListener;

  public static FontPickerDialog newInstance(FontPickerDialogListener fontPickerDialogListener) {
    FontPickerDialog fontPickerDialog = new FontPickerDialog();
    fontPickerDialog.mListener = fontPickerDialogListener;
    return fontPickerDialog;
  }

  // create callback method to pass back the selected font
  public interface FontPickerDialogListener {
    /**
     * This method is called when a font is selected in the FontPickerDialog
     *
     * @param dialog The dialog used to pick the font. Use dialog.getSelectedFont() to access the
     *     pathname of the chosen font
     */
    void onFontSelected(FontPickerDialog dialog);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    Context mContext = getActivity();

    HashMap<String, String> fonts = FontManager.getFontsMap();
    mFontPaths = new ArrayList<>();
    mFontNames = new ArrayList<>();

    for (String path : fonts.keySet()) {
      mFontPaths.add(path);
      mFontNames.add(fonts.get(path));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();

    builder.setView(inflater.inflate(R.layout.dialog_font_picker, null));

    FontAdapter adapter = new FontAdapter(mContext);
    builder.setAdapter(
        adapter,
        new OnClickListener() {

          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            mSelectedFont = mFontPaths.get(arg1);
            mListener.onFontSelected(FontPickerDialog.this);
          }
        });

    LinearLayout titleLinearLayout = new LinearLayout(mContext);

    titleLinearLayout.setLayoutParams(
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    titleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

    TextView title = new TextView(mContext);
    title.setText(R.string.select_a_font);
    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
    title.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    title.setPadding(16, 16, 16, 16);

    titleLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.accent_cyan));
    titleLinearLayout.addView(title);

    builder.setCustomTitle(titleLinearLayout);

    builder.setNegativeButton(
        R.string.cancel,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // don't have to do anything on cancel
          }
        });

    return builder.create();
  }

  /**
   * Callback method that is called once a font has been selected and the fontpickerdialog closes.
   *
   * @return The pathname of the font that was selected
   */
  public String getSelectedFont() {
    return mSelectedFont;
  }

  private class FontAdapter extends BaseAdapter {
    private Context mContext;

    public FontAdapter(Context c) {
      mContext = c;
    }

    @Override
    public int getCount() {
      return mFontNames.size();
    }

    @Override
    public Object getItem(int position) {
      return mFontNames.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) convertView;

      if (view == null) {
        view = new TextView(mContext);

      } else {
        view = (TextView) convertView;
      }

      view.setPadding(8, 8, 8, 8);
      Typeface tface = Typeface.createFromFile(mFontPaths.get(position));
      view.setTypeface(tface);
      view.setText(mFontNames.get(position));
      view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

      return view;
    }
  }
}
