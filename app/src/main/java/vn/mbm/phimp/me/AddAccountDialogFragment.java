package vn.mbm.phimp.me;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import vn.mbm.phimp.me.feedservice.FacebookActivity;

public class AddAccountDialogFragment extends android.support.v4.app.DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_account)
                .setItems(new String[]{
                        "Facebook",
                        "Wordpress"
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent fbauth = new Intent(getContext(), FacebookActivity.class);
                                getActivity().startActivity(fbauth);

                                PhimpMe.add_account_upload = true;
                                PhimpMe.add_account_setting = true;
                                break;
                            case 1:
                                getActivity().showDialog(5); // DIALOG_ADD_ACCOUNT_WORDPRESS = 5
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
