package org.fossasia.phimpme.gallery.util;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drew.lang.GeoLocation;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.MediaDetailsMap;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by dnld on 19/05/16.
 */
public class AlertDialogsHelper {

    public static boolean check=false;

    public static AlertDialog getInsertTextDialog(final ThemedActivity activity, AlertDialog.Builder dialogBuilder , EditText editText, @StringRes int title, String link) {

        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
        TextView textViewTitle = dialogLayout.findViewById(R.id.rename_title);

        ((CardView) dialogLayout.findViewById(R.id.dialog_chose_provider_title)).setCardBackgroundColor(activity.getCardBackgroundColor());
        textViewTitle.setBackgroundColor(activity.getPrimaryColor());
        if(link!=null){
            textViewTitle.setText( Html.fromHtml(link));
            textViewTitle.setLinkTextColor(Color.WHITE);
        }else{
            textViewTitle.setText(title);
        }
        textViewTitle.setMovementMethod(LinkMovementMethod.getInstance());
        ThemeHelper.setCursorDrawableColor(editText, activity.getTextColor());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);
        editText.setSingleLine(true);
        editText.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_IN);
        editText.setTextColor(activity.getTextColor());

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, null);
        } catch (Exception ignored) { }

        ((RelativeLayout) dialogLayout.findViewById(R.id.container_edit_text)).addView(editText);

        dialogBuilder.setView(dialogLayout);
        return dialogBuilder.create();
    }

    public static AlertDialog getTextDialog(final ThemedActivity activity, AlertDialog.Builder textDialogBuilder, @StringRes int title, @StringRes int Message, String msg){
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_text, null);

        TextView dialogTitle = dialogLayout.findViewById(R.id.text_dialog_title);
        TextView dialogMessage = dialogLayout.findViewById(R.id.text_dialog_message);

        ((CardView) dialogLayout.findViewById(R.id.message_card)).setCardBackgroundColor(activity.getCardBackgroundColor());
        dialogTitle.setBackgroundColor(activity.getPrimaryColor());
        dialogTitle.setText(title);
        if (msg != null) dialogMessage.setText(msg);
        else dialogMessage.setText(Message);
        dialogMessage.setTextColor(activity.getTextColor());
        textDialogBuilder.setView(dialogLayout);
        return textDialogBuilder.create();
    }

    public static AlertDialog getTextCheckboxDialog(final ThemedActivity activity, AlertDialog.Builder
            textDialogBuilder, @StringRes int title, @StringRes int Message, String msg, String checkboxmessage,
                                                    final int colorId){
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        TextView dialogTitle = dialogLayout.findViewById(R.id.text_dialog_title);
        TextView dialogMessage = dialogLayout.findViewById(R.id.text_dialog_message);
        TextView checkboxmessg = dialogLayout.findViewById(R.id.checkbox_text_dialog);
        final CheckBox checkBox = dialogLayout.findViewById(R.id.checkbox_text_dialog_cb);
        if(checkBox.isChecked()){
           check = true;
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    check=true;

                }else{
                    check=false;
                }
            }
        });
        ((CardView) dialogLayout.findViewById(R.id.message_card)).setCardBackgroundColor(activity.getCardBackgroundColor());
        dialogTitle.setBackgroundColor(activity.getPrimaryColor());
        dialogTitle.setText(title);
        checkboxmessg.setText(checkboxmessage);
        checkboxmessg.setTextColor(activity.getTextColor());
        if (msg != null) dialogMessage.setText(msg);
        else dialogMessage.setText(Message);
        dialogMessage.setTextColor(activity.getTextColor());
        textDialogBuilder.setView(dialogLayout);
        checkBox.setButtonTintList(ColorStateList.valueOf(colorId));
        return textDialogBuilder.create();
    }

    public static AlertDialog getProgressDialog(final ThemedActivity activity, AlertDialog.Builder progressDialog, String title, String message){
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_progress, null);
        TextView dialogTitle = dialogLayout.findViewById(R.id.progress_dialog_title);
        TextView dialogMessage = dialogLayout.findViewById(R.id.progress_dialog_text);

        dialogTitle.setBackgroundColor(activity.getPrimaryColor());
        ((CardView) dialogLayout.findViewById(R.id.progress_dialog_card)).setCardBackgroundColor(activity.getCardBackgroundColor());
        ((ProgressBar) dialogLayout.findViewById(R.id.progress_dialog_loading)).getIndeterminateDrawable().setColorFilter(activity.getPrimaryColor(),
                PorterDuff.Mode.SRC_ATOP);

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogMessage.setTextColor(activity.getTextColor());

        progressDialog.setCancelable(false);
        progressDialog.setView(dialogLayout);
        return progressDialog.create();
    }

    public static AlertDialog getDetailsDialog(final ThemedActivity activity, AlertDialog.Builder detailsDialogBuilder, final Media f) {
        MediaDetailsMap<String, String> mainDetails = f.getMainDetails(activity.getApplicationContext());
        final View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_media_detail, null);
        ImageView imgMap = dialogLayout.findViewById(R.id.photo_map);
        dialogLayout.findViewById(R.id.details_title).setBackgroundColor(activity.getPrimaryColor());
        ((CardView) dialogLayout.findViewById(R.id.photo_details_card)).setCardBackgroundColor(activity.getCardBackgroundColor());

        final GeoLocation location;
        if ((location = f.getGeoLocation()) != null) {
            PreferenceUtil SP = PreferenceUtil.getInstance(activity.getApplicationContext());

            StaticMapProvider staticMapProvider = StaticMapProvider.fromValue(
                    SP.getInt(activity.getString(R.string.preference_map_provider), StaticMapProvider.GOOGLE_MAPS.getValue()));

            Glide.with(activity.getApplicationContext())
                    .load(staticMapProvider.getUrl(location))
                    .asBitmap()
                    .centerCrop()
                    .animate(R.anim.fade_in)
                    .into(imgMap);

            imgMap.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d", location.getLatitude(), location.getLongitude(), 17);
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                }
            });

            imgMap.setVisibility(View.VISIBLE);
            dialogLayout.findViewById(R.id.details_title).setVisibility(View.GONE);

        }

        final TextView showMoreText = dialogLayout.findViewById(R.id.details_showmore);
        showMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreDetails(dialogLayout, activity, f);
                showMoreText.setVisibility(View.INVISIBLE);
            }
        });

        detailsDialogBuilder.setView(dialogLayout);
        loadDetails(dialogLayout,activity, mainDetails);
        return detailsDialogBuilder.create();
    }

    public static AlertDialog getAlbumDetailsDialog(final ThemedActivity activity, AlertDialog.Builder detailsDialogBuilder, final Album f) {
        MediaDetailsMap<String, String> mainDetails = f.getAlbumDetails(activity.getApplicationContext());
        final View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_album_detail, null);
        dialogLayout.findViewById(R.id.album_details_title).setBackgroundColor(activity.getPrimaryColor());
        ((CardView) dialogLayout.findViewById(R.id.album_details_card)).setCardBackgroundColor(activity.getCardBackgroundColor());
        detailsDialogBuilder.setView(dialogLayout);
        loadDetails(dialogLayout,activity, mainDetails);
        return detailsDialogBuilder.create();
    }

    private static void loadDetails(View dialogLayout, ThemedActivity activity, MediaDetailsMap<String, String> metadata) {
        int textColor = activity.getBaseTheme() != ThemeHelper.LIGHT_THEME ? Color.parseColor("#FAFAFA" ): Color.parseColor("#2b2b2b");
        ((ImageView)dialogLayout.findViewById(R.id.icon_folder)).setColorFilter(activity.getAccentColor());
        TextView name = dialogLayout.findViewById(R.id.album_details_name);
        name.setText(metadata.get(activity.getString(R.string.folder_name)));
        name.setTextColor(textColor);
        TextView type = dialogLayout.findViewById(R.id.album_details_type);
        type.setText(R.string.folder);
        type.setTextColor(textColor);
        TextView path = dialogLayout.findViewById(R.id.album_details_path);
        path.setText(metadata.get(activity.getString(R.string.folder_path)));
        path.setTextColor(textColor);
        TextView parent = dialogLayout.findViewById(R.id.album_details_parent);
        parent.setText(metadata.get(activity.getString(R.string.parent_path)));
        parent.setTextColor(textColor);
        TextView total = dialogLayout.findViewById(R.id.album_details_total);
        total.setText(metadata.get(activity.getString(R.string.total_photos)));
        total.setTextColor(textColor);
        TextView size = dialogLayout.findViewById(R.id.album_details_size);
        size.setText(metadata.get(activity.getString(R.string.size_folder)));
        size.setTextColor(textColor);
        TextView modified = dialogLayout.findViewById(R.id.album_details_last_modified);
        modified.setText(metadata.get(activity.getString(R.string.modified)));
        modified.setTextColor(textColor);
        TextView readable = dialogLayout.findViewById(R.id.album_details_readable);
        readable.setText(metadata.get(activity.getString(R.string.readable)));
        readable.setTextColor(textColor);
        TextView writable = dialogLayout.findViewById(R.id.album_details_writable);
        writable.setText(metadata.get(activity.getString(R.string.writable)));
        writable.setTextColor(textColor);
        TextView hidden = dialogLayout.findViewById(R.id.album_details_hidden);
        hidden.setText(metadata.get(activity.getString(R.string.hidden)));
        hidden.setTextColor(textColor);

        // Setting the Label text colors
        ((TextView)dialogLayout.findViewById(R.id.label_type)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_path)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_parent)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_total_photos)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_size)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_last_modified)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_readable)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_writable)).setTextColor(textColor);
        ((TextView)dialogLayout.findViewById(R.id.label_hidden)).setTextColor(textColor);
    }

    private static void showMoreDetails(View dialogLayout, ThemedActivity activity, Media media) {

        MediaDetailsMap<String, String> metadata = media.getAllDetails();
        loadDetails(dialogLayout ,activity , metadata);
    }

    public static void setButtonTextColor(int[] buttons, int color, AlertDialog alertDialog) {
        for(int button : buttons)
            alertDialog.getButton(button).setTextColor(color);
    }
}
