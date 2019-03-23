package org.fossasia.phimpme.gallery.activities;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.providers.MediaStoreProvider;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

/**
 * Created by dnld on 22/05/16.
 */
public class SecurityActivity extends ThemedActivity {

    private Toolbar toolbar;
    private LinearLayout llbody;
    private LinearLayout llroot;
    private PreferenceUtil SP;
    private SecurityHelper securityObj;
    private SwitchCompat swActiveSecurity;
    private SwitchCompat swApplySecurityDelete;
    private SwitchCompat swApplySecurityHidden;
    private SwitchCompat swApplySecurityFolder;
    public ArrayList<Album> albums;
    public ArrayList<String> securedfol = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_layout);
        ActivitySwitchHelper.setContext(this);
        SP = PreferenceUtil.getInstance(getApplicationContext());
        securityObj = new SecurityHelper(SecurityActivity.this);
        toolbar = findViewById(R.id.toolbar);
        albums = new ArrayList<>();
        albums = MediaStoreProvider.getAlbums(getApplicationContext());
        llbody = findViewById(R.id.ll_security_dialog_body);
        llroot = findViewById(R.id.root);
        LinearLayout llchangepassword = findViewById(R.id.ll_change_password);
        swApplySecurityDelete = findViewById(R.id.security_body_apply_delete_switch);
        swActiveSecurity = findViewById(R.id.active_security_switch);
        swApplySecurityHidden = findViewById(R.id.security_body_apply_hidden_switch);
        swApplySecurityFolder = findViewById(R.id.security_body_apply_folder_switch);

        /** - SWITCHES - **/
        /** - ACTIVE SECURITY - **/
        LinearLayout linearLayout=findViewById(R.id.ll_active_security);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean no = swActiveSecurity.isChecked();
                if(no){
                    swActiveSecurity.setChecked(false);
                }else {
                    swActiveSecurity.setChecked(true);
                }
            }
        });
        swActiveSecurity.setChecked(securityObj.isActiveSecurity());
        swActiveSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = SP.getEditor();
                securityObj.updateSecuritySetting();
                updateSwitchColor(swActiveSecurity, getAccentColor());
                llbody.setEnabled(swActiveSecurity.isChecked());
                if (isChecked) {
                    String mpassword = SP.getString(getString(R.string.preference_password_value),null);
                    if (mpassword != null) {
                        securityObj.updateSecuritySetting();
                        SnackBarHandler.show(llroot, R.string.remember_password_message);
                        swActiveSecurity.setChecked(true);
                        SP.putBoolean(getString(R.string.preference_use_password), true);
                        toggleEnabledChild(true);
                    }
                    else
                    setPasswordDialog();
                }
                else {
                    editor.putBoolean(getString(R.string.preference_use_password), false);
                    editor.commit();
                    toggleEnabledChild(false);
                    Snackbar.make(findViewById(android.R.id.content), R.string.no_password_set, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        updateSwitchColor(swActiveSecurity, getAccentColor());
        llbody.setEnabled(swActiveSecurity.isChecked());

        /** - ACTIVE SECURITY ON HIDDEN FOLDER - **/
        LinearLayout swHiddenFolder = findViewById(R.id.ll_security_body_apply_hidden);
        swHiddenFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean no = swApplySecurityHidden.isChecked();
                if(no && swActiveSecurity.isChecked()){
                    swApplySecurityHidden.setChecked(false);
                }else if(swActiveSecurity.isChecked()) {
                    swApplySecurityHidden.setChecked(true);
                }
            }
        });
        swApplySecurityHidden.setChecked(securityObj.isPasswordOnHidden());
        swApplySecurityHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(getString(R.string.preference_use_password_on_hidden), isChecked);
                securityObj.updateSecuritySetting();
                updateSwitchColor(swApplySecurityHidden, getAccentColor());
            }
        });
        updateSwitchColor(swApplySecurityHidden, getAccentColor());

        /**ACTIVE SECURITY ON LOCAL FOLDERS**/
        LinearLayout swLocalFolders = findViewById(R.id.ll_security_body_apply_security_local_folders);
        swLocalFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean no = swApplySecurityFolder.isChecked();
                if(no && swActiveSecurity.isChecked()){
                    swApplySecurityFolder.setChecked(false);
                }else if(swActiveSecurity.isChecked()){
                    swApplySecurityFolder.setChecked(true);
                }
            }
        });
        swApplySecurityFolder.setChecked(securityObj.isPasswordOnfolder());
        swApplySecurityFolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(getString(R.string.preference_use_password_on_folder), isChecked);
                securityObj.updateSecuritySetting();
                updateSwitchColor(swApplySecurityFolder, getAccentColor());
                if(isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this, getDialogStyle());
                    View view = getLayoutInflater().inflate(R.layout.dialog_security_folder, null);
                    view.setBackgroundColor(getBackgroundColor());
                    TextView title = view.findViewById(R.id.titlesecure);
                    LinearLayout linearLayout = view.findViewById(R.id.titlelayout);
                    linearLayout.setBackgroundColor(getAccentColor());
                    title.setBackgroundColor(getAccentColor());
                    title.setText(R.string.choose_folders);
                    RecyclerView recyclerView = view.findViewById(R.id.secure_folder_recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    final SecureDialogAdapter securedLocalFolders = new SecureDialogAdapter();
                    recyclerView.setAdapter(securedLocalFolders);
                    builder.setView(view);
                    AlertDialog ad = builder.create();
                    ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string
                            .ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(securedfol.size()>0){
                                SharedPreferences.Editor editor = SP.getEditor();
                                Gson gson = new Gson();
                                String securedfolders = gson.toJson(securedfol);
                                editor.putString(getString(R.string.preference_use_password_secured_local_folders), securedfolders);
                                editor.commit();
                            }else{
                                SP.putBoolean(getString(R.string.preference_use_password_on_folder), false);
                                securityObj.updateSecuritySetting();
                                swApplySecurityFolder.setChecked(false);
                                updateSwitchColor(swApplySecurityFolder, getAccentColor());
                            }
                        }});
                    ad.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(securedfol.size()>0){
                               for(Album a: albums){
                                   if(a.getsecured()){
                                       a.setsecured(false);
                                   }
                               }
                            }
                            dialogInterface.dismiss();
                            SP.putBoolean(getString(R.string.preference_use_password_on_folder), false);
                            securityObj.updateSecuritySetting();
                            swApplySecurityFolder.setChecked(false);
                            updateSwitchColor(swApplySecurityFolder, getAccentColor());
                        }
                    });
                    ad.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if(keyCode ==  KeyEvent.KEYCODE_BACK){
                                dialog.dismiss();
                                SP.putBoolean(getString(R.string.preference_use_password_on_folder), false);
                                securityObj.updateSecuritySetting();
                                swApplySecurityFolder.setChecked(false);
                                updateSwitchColor(swApplySecurityFolder, getAccentColor());
                            }
                            return true;
                        }
                    });
                    ad.show();
                    ad.setCanceledOnTouchOutside(false);
                    ad.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getAccentColor());
                    ad.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getAccentColor());
                }else{
                    SP.putBoolean(getString(R.string.preference_use_password_on_folder), false);
                    securityObj.updateSecuritySetting();
                    updateSwitchColor(swApplySecurityFolder, getAccentColor());
                }
            }
        });
        updateSwitchColor(swApplySecurityFolder, getAccentColor());

        llchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swActiveSecurity.isChecked())
                    changePasswordDialog();
                else
                    SnackBarHandler.show(llroot, R.string.set_passowrd);
            }
        });

        /**ACTIVE SECURITY ON DELETE ACTION**/
        LinearLayout swDeleteAction = findViewById(R.id.ll_security_body_apply_delete);
        swDeleteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean no = swApplySecurityDelete.isChecked();
                if(no && swActiveSecurity.isChecked()){
                    swApplySecurityDelete.setChecked(false);
                }else if (swActiveSecurity.isChecked()){
                    swApplySecurityDelete.setChecked(true);
                }
            }
        });
        swApplySecurityDelete.setChecked(securityObj.isPasswordOnDelete());
        swApplySecurityDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(getString(R.string.preference_use_password_on_delete), isChecked);
                securityObj.updateSecuritySetting();
                updateSwitchColor(swApplySecurityDelete, getAccentColor());
            }
        });

        updateSwitchColor(swApplySecurityDelete, getAccentColor());
        setupUI();
        toggleEnabledChild(swActiveSecurity.isChecked());
    }

    private void setPasswordDialog() {

        final short max_password_length = 128;
        final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(SecurityActivity.this, getDialogStyle());
        final View PasswordDialogLayout = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        final TextView passwordDialogTitle = PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        final TextView security_title = PasswordDialogLayout.findViewById(R.id.security_question_title);
        final CheckBox checkBox = PasswordDialogLayout.findViewById(R.id.set_password_checkbox);
        checkBox.setText(getResources().getString(R.string.show_password));
        checkBox.setTextColor(getTextColor());
        final CardView passwordDialogCard = PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editTextPassword = PasswordDialogLayout.findViewById(R.id.password_edittxt);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText editTextConfirmPassword = PasswordDialogLayout.findViewById(R.id.confirm_password_edittxt);
        final EditText securityAnswer1 = PasswordDialogLayout.findViewById(R.id.security_answer_edittext);
        final EditText securityQuestion = PasswordDialogLayout.findViewById(R.id.security_question_edittext);
        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setButtonTintList(ColorStateList.valueOf(getAccentColor()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    checkBox.setButtonTintList(ColorStateList.valueOf(getAccentColor()));
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    checkBox.setButtonTintList(ColorStateList.valueOf(getAccentColor()));
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().toString().length());
            }

            @Override public void afterTextChanged(Editable editable) {
                if(editable.length() == max_password_length) {
                    editTextConfirmPassword.setText(editable.toString().substring(0, max_password_length-1));
                    editTextConfirmPassword.setSelection(max_password_length-1);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextPassword.setSelection(editTextPassword.getText().toString().length());
            }

            @Override public void afterTextChanged(Editable editable) {
                if(editable.length() == max_password_length) {
                    editTextPassword.setText(editable.toString().substring(0, max_password_length-1));
                    editTextPassword.setSelection(max_password_length-1);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        passwordDialogTitle.setText(R.string.type_password);
        passwordDialogTitle.setBackgroundColor(getPrimaryColor());
        passwordDialogCard.setBackgroundColor(getCardBackgroundColor());
        editTextPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextPassword.setTextColor(getTextColor());
        editTextPassword.setHint(R.string.password);
        editTextPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextPassword, getTextColor());
        editTextConfirmPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextConfirmPassword.setTextColor(getTextColor());
        editTextConfirmPassword.setHint(R.string.confirm_password);
        editTextConfirmPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextConfirmPassword, getTextColor());
        security_title.setTextColor(getTextColor());
        securityAnswer1.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityAnswer1.setTextColor(getTextColor());
        securityAnswer1.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(securityAnswer1, getTextColor());
        securityQuestion.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityQuestion.setTextColor(getTextColor());
        securityQuestion.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(securityQuestion, getTextColor());
        passwordDialog.setView(PasswordDialogLayout);

        final AlertDialog dialog = passwordDialog.create();
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                swActiveSecurity.setChecked(false);
                SP.putBoolean(getString(R.string.preference_use_password), false);
                dialog.dismiss();
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ok_action).toUpperCase(), (DialogInterface.OnClickListener) null);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean changed = false;

                        if (editTextPassword.length() > 3) {
                            if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                                if (securityQuestion.getText().length() != 0) {
                                    if (securityAnswer1.getText().length() != 0) {
                                        SP.putString(getString(R.string.preference_password_value), editTextPassword.getText().toString());
                                        SP.putString(getString(R.string.security_question), securityQuestion.getText().toString());
                                        SP.putString(getString(R.string.security_answer), securityAnswer1.getText().toString());
                                        securityObj.updateSecuritySetting();
                                        SnackBarHandler.show(llroot, R.string.remember_password_message);
                                        changed = true;
                                        dialog.dismiss();
                                        Snackbar.make(findViewById(android.R.id.content), R.string.password_set, Snackbar.LENGTH_SHORT)
                                                .show();
                                        swActiveSecurity.setChecked(changed);
                                        SP.putBoolean(getString(R.string.preference_use_password), changed);
                                        toggleEnabledChild(changed);
                                    }else{
                                        securityAnswer1.requestFocus();
                                        securityAnswer1.setError(getString(R.string.security_ans_empty));
                                    }
                                }else{
                                    securityQuestion.requestFocus();
                                    securityQuestion.setError(getString(R.string.security_ques_empty));
                                }
                            } else{
                                editTextConfirmPassword.requestFocus();
                                editTextConfirmPassword.setError(getString(R.string.password_dont_match));
                        }
                    } else {
                        editTextPassword.requestFocus();
                        editTextPassword.setError(getString( R.string.error_password_length));
                    }
                    }
                });
            }

            });

        dialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), dialog);
    }

     private void changePasswordDialog() {

        final short max_password_length = 128;
        final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(SecurityActivity.this, getDialogStyle());
        final View PasswordDialogLayout = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        final TextView passwordDialogTitle = PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        final TextView security_title = PasswordDialogLayout.findViewById(R.id.security_question_title);
        CheckBox checkBox = PasswordDialogLayout.findViewById(R.id.set_password_checkbox);
        checkBox.setText(getResources().getString(R.string.show_password));
        checkBox.setTextColor(getTextColor());
        final CardView passwordDialogCard = PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editTextPassword = PasswordDialogLayout.findViewById(R.id.password_edittxt);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText editTextConfirmPassword = PasswordDialogLayout.findViewById(R.id.confirm_password_edittxt);
        final EditText securityAnswer1 = PasswordDialogLayout.findViewById(R.id.security_answer_edittext);
        final EditText securityQuestion = PasswordDialogLayout.findViewById(R.id.security_question_edittext);
        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setButtonTintList(ColorStateList.valueOf(getAccentColor()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().toString().length());
            }
            @Override public void afterTextChanged(Editable editable) {
                if(editable.length() == max_password_length) {
                    editTextConfirmPassword.setText(editable.toString().substring(0, max_password_length-1));
                    editTextConfirmPassword.setSelection(max_password_length-1);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT).show();
                }
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextPassword.setSelection(editTextPassword.getText().toString().length());
            }
            @Override public void afterTextChanged(Editable editable) {
                if(editable.length() == max_password_length) {
                    editTextPassword.setText(editable.toString().substring(0, max_password_length-1));
                    editTextPassword.setSelection(max_password_length-1);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        passwordDialogTitle.setText(R.string.change_password);
        passwordDialogTitle.setBackgroundColor(getPrimaryColor());
        passwordDialogCard.setBackgroundColor(getCardBackgroundColor());
        editTextPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextPassword.setTextColor(getTextColor());
        editTextPassword.setHint(R.string.new_password);
        editTextPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextPassword, getTextColor());
        editTextConfirmPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextConfirmPassword.setTextColor(getTextColor());
        editTextConfirmPassword.setHint(R.string.confirm_new_password);
        editTextConfirmPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextConfirmPassword, getTextColor());
        security_title.setTextColor(getTextColor());
        securityAnswer1.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityAnswer1.setTextColor(getTextColor());
        securityAnswer1.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(securityAnswer1, getTextColor());
        securityQuestion.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityQuestion.setTextColor(getTextColor());
        securityQuestion.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(securityQuestion, getTextColor());
        passwordDialog.setView(PasswordDialogLayout);

        final AlertDialog dialog = passwordDialog.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
         dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ok_action).toUpperCase(), (DialogInterface.OnClickListener) null);
         dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

         dialog.setOnShowListener(new DialogInterface.OnShowListener() {
             @Override
             public void onShow(DialogInterface dialogInterface) {
                 Button b = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                 b.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         if (editTextPassword.length() > 3) {
                             if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                                 if (securityQuestion.getText().length() != 0) {
                                     if (securityAnswer1.getText().length() != 0) {
                                         SP.putString(getString(R.string.preference_password_value), editTextPassword.getText().toString());
                                         SP.putString(getString(R.string.security_question), securityQuestion.getText().toString());
                                         SP.putString(getString(R.string.security_answer), securityAnswer1.getText().toString());
                                         securityObj.updateSecuritySetting();
                                         SnackBarHandler.show(llroot, R.string.remember_password_message);
                                         dialog.dismiss();
                                         Snackbar.make(findViewById(android.R.id.content), R.string.password_changed, Snackbar.LENGTH_SHORT)
                                                 .show();

                                         }else{
                                         securityAnswer1.requestFocus();
                                         securityAnswer1.setError(getString(R.string.security_ans_empty));
                                     }
                                 }else{
                                     securityQuestion.requestFocus();
                                     securityQuestion.setError(getString(R.string.security_ques_empty));
                                 }
                             } else{
                                 editTextConfirmPassword.requestFocus();
                                 editTextConfirmPassword.setError(getString(R.string.password_dont_match));
                             }
                         } else {
                             editTextPassword.requestFocus();
                             editTextPassword.setError(getString( R.string.error_password_length));
                         }
                     }
                 });
             }

         });

        dialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), dialog);
    }
    
    private void toggleEnabledChild(boolean enable) {
        if (!enable) {
            swApplySecurityDelete.setChecked(enable);
            swApplySecurityHidden.setChecked(enable);
            swApplySecurityFolder.setChecked(enable);
        }
        swApplySecurityDelete.setEnabled(enable);
        swApplySecurityHidden.setEnabled(enable);
        swApplySecurityFolder.setEnabled(enable);
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarColor();
    }

    private void setupUI() {
        setNavBarColor();
        toolbar.setBackgroundColor(getPrimaryColor());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_arrow_back)
                        .color(Color.WHITE)
                        .sizeDp(19));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(getString(R.string.about));

        IconicsImageView imgActiveSecurity = findViewById(R.id.active_security_icon);
        TextView txtActiveSecurity = findViewById(R.id.active_security_item_title);
        IconicsImageView imgActiveSecurityChangePassword = findViewById(R.id.security_change_password);
        TextView txtChangePassword = findViewById(R.id.active_security_change_password_title);
        TextView txtApplySecurity = findViewById(R.id.security_body_apply_on);
        IconicsImageView imgApplySecurityHidden = findViewById(R.id.security_body_apply_hidden_icon);
        TextView txtApplySecurityHidden = findViewById(R.id.security_body_apply_hidden_title);
        IconicsImageView imgApplySecurityDelete = findViewById(R.id.security_body_apply_delete_icon);
        TextView txtApplySecurityDelete = findViewById(R.id.security_body_apply_delete_title);
        IconicsImageView folderActiveSecurity = findViewById(R.id.security_body_apply_folder_icon);
        TextView folActiveSecurity = findViewById(R.id.security_body_apply_folders_title);
        CardView securityDialogCard = findViewById(R.id.security_dialog_card);
        llroot.setBackgroundColor(getBackgroundColor());
        securityDialogCard.setCardBackgroundColor(getCardBackgroundColor());

        /*ICONS*/
        int color = getIconColor();
        imgActiveSecurity.setColor(color);
        imgActiveSecurityChangePassword.setColor(color);
        imgApplySecurityHidden.setColor(color);
        imgApplySecurityDelete.setColor(color);
        folderActiveSecurity.setColor(color);

        /*TEXTVIEWS*/
        color = getTextColor();
        txtActiveSecurity.setTextColor(color);
        txtChangePassword.setTextColor(color);
        txtApplySecurity.setTextColor(color);
        txtApplySecurityHidden.setTextColor(color);
        txtApplySecurityDelete.setTextColor(color);
        folActiveSecurity.setTextColor(color);
    }

    private class SecureDialogAdapter extends RecyclerView.Adapter<SecureDialogAdapter.ViewHolder>{

        SecureDialogAdapter(){}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.secure_folder_dialog_item_view, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {
            final Album a = albums.get(position);
            holder.foldername.setText(a.getName());
            holder.foldername.setTextColor(getTextColor());
            holder.foldercheckbox.setOnCheckedChangeListener(null);
            holder.foldercheckbox.setChecked(a.getsecured());
            //name of the folder can be clicked to check the box instead of the checkbox itself
            TextView folderName = holder.foldername;
            folderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean no = holder.foldercheckbox.isChecked();
                    if (no) {
                        holder.foldercheckbox.setChecked(false);
                    } else holder.foldercheckbox.setChecked(true);
                }
            });
            //holder.foldercheckbox.setButtonTintList();
            holder.foldercheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        securedfol.add(a.getPath());
                        a.setsecured(true);
                    }else{
                        securedfol.remove(a.getPath());
                        a.setsecured(false);
                    }
                }
            });
            holder.foldercheckbox.setButtonTintList(ColorStateList.valueOf(getAccentColor()));
        }

        @Override public int getItemCount() {
            return albums.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private TextView foldername;
            private CheckBox foldercheckbox;

            public ViewHolder(View itemView) {
                super(itemView);
                foldername = itemView.findViewById(R.id.foldername);
                if (getBaseTheme() == ThemeHelper.LIGHT_THEME) {
                    CheckBox checkBox = itemView.findViewById(R.id.secure_folder_checkbox_black);
                    checkBox.setVisibility(View.VISIBLE);
                    foldercheckbox = itemView.findViewById(R.id.secure_folder_checkbox_black);
                } else if (getBaseTheme() == ThemeHelper.DARK_THEME || getBaseTheme() == ThemeHelper.AMOLED_THEME) {
                    CheckBox checkBox = itemView.findViewById(R.id.secure_folder_checkbox_white);
                    checkBox.setVisibility(View.VISIBLE);
                    foldercheckbox = itemView.findViewById(R.id.secure_folder_checkbox_white);
                }
            }
        }
    }
}
