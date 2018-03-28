package org.fossasia.phimpme.gallery.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;

import com.google.gson.Gson;

/**
 * Created by Jibo on 06/05/2016.
 */

public class SecurityHelper {
    private boolean activeSecurity;
    private boolean passwordOnDelete;
    private boolean passwordOnHidden;
    private boolean passwordOnfolder;
    private String passwordValue;
    private String[] securedfolders;
    private TextInputLayout passwordTextInputLayout;

    private Context context;
    public SecurityHelper(Context context){
        this.context = context;
        updateSecuritySetting();
    }

    public boolean isActiveSecurity(){return activeSecurity;}
    public boolean isPasswordOnHidden(){return passwordOnHidden;}
    public boolean isPasswordOnDelete(){return passwordOnDelete;}
    public boolean isPasswordOnfolder(){return passwordOnfolder;}

    public boolean checkPassword(String pass){
        return (isActiveSecurity() && pass.equals(passwordValue));
    }

    public void updateSecuritySetting(){
        PreferenceUtil SP = PreferenceUtil.getInstance(context);
        String secur = SP.getString(context.getString(R.string.preference_use_password_secured_local_folders), "");
        Gson gson = new Gson();
        String[] secfo = gson.fromJson(secur, String[].class);
        if(secfo!=null){
            this.securedfolders = secfo;
        }
        this.activeSecurity = SP.getBoolean(context.getString(R.string.preference_use_password), false);
        this.passwordOnDelete = SP.getBoolean(context.getString(R.string.preference_use_password_on_delete), false);
        this.passwordOnHidden = SP.getBoolean(context.getString(R.string.preference_use_password_on_hidden), true);
        this.passwordValue = SP.getString(context.getString(R.string.preference_password_value), "");
       // this.securedfolders = SP.get
        this.passwordOnfolder = SP.getBoolean(context.getString(R.string.preference_use_password_on_folder), false);
    }

    public EditText getInsertPasswordDialog(final ThemedActivity activity, AlertDialog.Builder passwordDialog){

        final View PasswordDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editxtPassword = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        passwordTextInputLayout = (TextInputLayout) PasswordDialogLayout.findViewById(R.id.password_text_input_layout);
        passwordTextInputLayout.setError(context.getString(R.string.wrong_password));
        CheckBox checkBox = (CheckBox) PasswordDialogLayout.findViewById(R.id.show_password_checkbox);
        checkBox.setText(context.getResources().getString(R.string.show_password));
        checkBox.setTextColor(activity.getTextColor());
        editxtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    editxtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    int position = editxtPassword.getText().length();
                    Editable editObj= editxtPassword.getText();
                    Selection.setSelection(editObj, position);
                }else{
                    editxtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    int position = editxtPassword.getText().length();
                    Editable editObj= editxtPassword.getText();
                    Selection.setSelection(editObj, position);
                }
            }
        });
        passwordTextInputLayout.setVisibility(View.GONE);

        passwordDialogTitle.setBackgroundColor(activity.getPrimaryColor());
        passwordDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        ThemeHelper.setCursorDrawableColor(editxtPassword, activity.getTextColor());
        editxtPassword.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editxtPassword.setTextColor(activity.getTextColor());
        passwordDialog.setView(PasswordDialogLayout);
        return editxtPassword;
    }

    public String[] getSecuredfolders(){
        return securedfolders;
    }

    public TextInputLayout getTextInputLayout(){
        return passwordTextInputLayout;
    }
}
