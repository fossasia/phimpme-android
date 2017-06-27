package org.fossasia.phimpme.leafpic.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.data.local.ImageDescModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class DescriptionActivity extends AppCompatActivity {

    private Realm realm;
    private DatabaseHelper databaseHelper;

    @BindView(R.id.activity_description_editText)
    EditText editText;

    ImageDescModel temp;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        databaseHelper =new DatabaseHelper(realm);

        path=getIntent().getStringExtra("path");

        temp= databaseHelper.getImageDesc(path);
        
        //if description exists, display it
        if(temp != null) {
            editText.setText(temp.getTitle());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        temp= databaseHelper.getImageDesc(path);
        if(item.getItemId() == R.id.activity_description_save) {
            if(temp == null) {
                databaseHelper.addImageDesc(new ImageDescModel(path,editText.getText().toString()));
            } else {
                //temp.setTitle(editText.getText().toString());
                databaseHelper.update(new ImageDescModel(path, editText.getText().toString()));
            }
        }
        return true;
    }


}
