package org.fossasia.phimpme.leafpic.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.leafpic.data.RealmController;
import org.fossasia.phimpme.leafpic.data.providers.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DescriptionActivity extends AppCompatActivity {

    RealmController realmController=new RealmController();

    @BindView(R.id.activity_description_editText)
    EditText editText;

    Item temp;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ButterKnife.bind(this);

        //realmController=new RealmController();
        path=getIntent().getStringExtra("path");

        temp=realmController.getItem(path);


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

        temp=realmController.getItem(path);
        if(item.getItemId() == R.id.activity_description_save) {
            if(temp == null) {
                realmController.addItem(new Item(path,editText.getText().toString()));
            } else {
                //temp.setTitle(editText.getText().toString());
                realmController.update(new Item(path, editText.getText().toString()));
            }
        }
        return true;
    }


}
