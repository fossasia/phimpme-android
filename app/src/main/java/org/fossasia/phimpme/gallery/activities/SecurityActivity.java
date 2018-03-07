package org.fossasia.phimpme.gallery.activities;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        albums = new ArrayList<>();
        albums = MediaStoreProvider.getAlbums(getApplicationContext());
        llbody = (LinearLayout) findViewById(R.id.ll_security_dialog_body);
        llroot = (LinearLayout) findViewById(R.id.root);
        LinearLayout llchangepassword = (LinearLayout) findViewById(R.id.ll_change_password);
        swApplySecurityDelete = (SwitchCompat) findViewById(R.id.security_body_apply_delete_switch);
        swActiveSecurity = (SwitchCompat) findViewById(R.id.active_security_switch);
        swApplySecurityHidden = (SwitchCompat) findViewById(R.id.security_body_apply_hidden_switch);
        swApplySecurityFolder = (SwitchCompat) findViewById(R.id.security_body_apply_folder_switch);

        /** - SWITCHES - **/
        /** - ACTIVE SECURITY - **/
        swActiveSecurity.setChecked(securityObj.isActiveSecurity());
        swActiveSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = SP.getEditor();
                securityObj.updateSecuritySetting();
                updateSwitchColor(swActiveSecurity, getAccentColor());
                llbody.setEnabled(swActiveSecurity.isChecked());
                if (isChecked)
                    setPasswordDialog();
                else {
                    editor.putString(getString(R.string.preference_password_value), "");
                    editor.putBoolean(getString(R.string.preference_use_password), false);
                    editor.commit();
                    toggleEnabledChild(false);
                }
            }
        });
        updateSwitchColor(swActiveSecurity, getAccentColor());
        llbody.setEnabled(swActiveSecurity.isChecked());

        /** - ACTIVE SECURITY ON HIDDEN FOLDER - **/
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
                    TextView title = (TextView) view.findViewById(R.id.titlesecure);
                    title.setText("Choose folders to secure");
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.secure_folder_recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    SecureDialogAdapter securedLocalFolders = new SecureDialogAdapter();
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

        final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(SecurityActivity.this, getDialogStyle());
        final View PasswordDialogLayout = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        CheckBox checkBox = (CheckBox) PasswordDialogLayout.findViewById(R.id.set_password_checkbox);
        checkBox.setText(getResources().getString(R.string.show_password));
        checkBox.setTextColor(getTextColor());
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editTextPassword = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText editTextConfirmPassword = (EditText) PasswordDialogLayout.findViewById(R.id.confirm_password_edittxt);
        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
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
                if(editable.length() == 11) {
                    editTextConfirmPassword.setText(editable.toString().substring(0, 10));
                    editTextConfirmPassword.setSelection(10);
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
                if(editable.length() == 11) {
                    editTextPassword.setText(editable.toString().substring(0, 10));
                    editTextPassword.setSelection(10);
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
        editTextPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextPassword, getTextColor());
        editTextConfirmPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextConfirmPassword.setTextColor(getTextColor());
        editTextConfirmPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextConfirmPassword, getTextColor());
        passwordDialog.setView(PasswordDialogLayout);

        AlertDialog dialog = passwordDialog.create();
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                swActiveSecurity.setChecked(false);
                SP.putBoolean(getString(R.string.preference_use_password), false);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean changed = false;

                if (editTextPassword.length() > 3) {
                    if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                        SP.putString(getString(R.string.preference_password_value), editTextPassword.getText().toString());
                        securityObj.updateSecuritySetting();
                        SnackBarHandler.show(llroot, R.string.remember_password_message);
                        changed = true;
                    } else
                        SnackBarHandler.show(llroot, R.string.password_dont_match);
                } else
                    SnackBarHandler.show(llroot, R.string.error_password_length);

                swActiveSecurity.setChecked(changed);
                SP.putBoolean(getString(R.string.preference_use_password), changed);
                toggleEnabledChild(changed);
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), dialog);
    }

     private void changePasswordDialog() {
        final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(SecurityActivity.this, getDialogStyle());
        final View PasswordDialogLayout = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        CheckBox checkBox = (CheckBox) PasswordDialogLayout.findViewById(R.id.set_password_checkbox);
        checkBox.setText(getResources().getString(R.string.show_password));
        checkBox.setTextColor(getTextColor());
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editTextPassword = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText editTextConfirmPassword = (EditText) PasswordDialogLayout.findViewById(R.id.confirm_password_edittxt);
        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
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
                if(editable.length() == 11) {
                    editTextConfirmPassword.setText(editable.toString().substring(0, 10));
                    editTextConfirmPassword.setSelection(10);
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
                if(editable.length() == 11) {
                    editTextPassword.setText(editable.toString().substring(0, 10));
                    editTextPassword.setSelection(10);
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
        editTextPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextPassword, getTextColor());
        editTextConfirmPassword.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextConfirmPassword.setTextColor(getTextColor());
        editTextConfirmPassword.setHintTextColor(getSubTextColor());
        setCursorDrawableColor(editTextConfirmPassword, getTextColor());
        passwordDialog.setView(PasswordDialogLayout);
        AlertDialog dialog = passwordDialog.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             // nothing is done.
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTextPassword.length() > 3) {
                   if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                       if (!editTextPassword.getText().toString().equals(SP.getString(getString(R.string.preference_password_value),"")))   {
                            SP.putString(getString(R.string.preference_password_value), editTextPassword.getText().toString());
                            securityObj.updateSecuritySetting();
                            SnackBarHandler.show(llroot, R.string.remember_password_message);
                        } else
                           SnackBarHandler.show(llroot, R.string.error_password_match);
                    } else
                       SnackBarHandler.show(llroot, R.string.password_dont_match);
                } else
                    SnackBarHandler.show(llroot, R.string.error_password_length);
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

        IconicsImageView imgActiveSecurity = (IconicsImageView) findViewById(R.id.active_security_icon);
        TextView txtActiveSecurity = (TextView) findViewById(R.id.active_security_item_title);
        IconicsImageView imgActiveSecurityChangePassword = (IconicsImageView) findViewById(R.id.security_change_password);
        TextView txtChangePassword = (TextView) findViewById(R.id.active_security_change_password_title);
        TextView txtApplySecurity = (TextView) findViewById(R.id.security_body_apply_on);
        IconicsImageView imgApplySecurityHidden = (IconicsImageView) findViewById(R.id.security_body_apply_hidden_icon);
        TextView txtApplySecurityHidden = (TextView) findViewById(R.id.security_body_apply_hidden_title);
        IconicsImageView imgApplySecurityDelete = (IconicsImageView) findViewById(R.id.security_body_apply_delete_icon);
        TextView txtApplySecurityDelete = (TextView) findViewById(R.id.security_body_apply_delete_title);
        IconicsImageView folderActiveSecurity = (IconicsImageView) findViewById(R.id.security_body_apply_folder_icon);
        TextView folActiveSecurity = (TextView) findViewById(R.id.security_body_apply_folders_title);
        CardView securityDialogCard = (CardView) findViewById(R.id.security_dialog_card);
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

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            final Album a = albums.get(position);
            holder.foldername.setText(a.getName());
            holder.foldername.setTextColor(getTextColor());
            holder.foldercheckbox.setOnCheckedChangeListener(null);
            holder.foldercheckbox.setChecked(a.getsecured());
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
        }

        @Override public int getItemCount() {
            return albums.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private TextView foldername;
            private CheckBox foldercheckbox;

            public ViewHolder(View itemView) {
                super(itemView);
                foldername = (TextView) itemView.findViewById(R.id.foldername);
                foldercheckbox = (CheckBox) itemView.findViewById(R.id.secure_folder_checkbox);
            }
        }
    }
}
