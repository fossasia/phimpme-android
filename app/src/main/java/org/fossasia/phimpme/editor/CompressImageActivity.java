package org.fossasia.phimpme.editor;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.editor.adapter.ListCompressAdapter;
import org.fossasia.phimpme.editor.utils.FileUtil;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.base.MediaDetailsMap;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class CompressImageActivity extends ThemedActivity {

    public String saveFilePath;
    public static final String EXTRA_OUTPUT = "extra_output";
    public int percentagecompress=0;
    public final int[] cwidth = new int[1];
    public final int[] cheight = new int[1];

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_image);
        initView();
        Button size=(Button)findViewById(R.id.size);
        size.setBackgroundColor(getAccentColor());
        Button dimension=(Button)findViewById(R.id.bypixel);
        dimension.setBackgroundColor(getAccentColor());
        ImageButton cancel=(ImageButton)findViewById(R.id.edit_cancel);
        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressSize();
            }
        });
        dimension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressDim();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

        ImageViewTouch imageViewToucher=(ImageViewTouch)findViewById(R.id.main_image);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageViewToucher);
        imageViewToucher.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    private void compressSize() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_compresssize, null);
        TextView title = (TextView) dialogLayout.findViewById(R.id.compress_title);
        title.setBackgroundColor(getPrimaryColor());
        SeekBar percentsize = (SeekBar) dialogLayout.findViewById(R.id.seekBar);
        percentsize.getThumb().setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN);
        percentsize.setProgress(0);
        final TextView percent=(TextView)dialogLayout.findViewById(R.id.textview2);
        percentsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //options of compress by size from 5% to 9;
                int progress1=95-progress;
                progress1=progress1-progress1%5;
                percent.setText(progress1+"%");
                percentagecompress=progress1;}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(dialogLayout);
        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.set), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SaveCompressedImage().execute("Size");
                finish();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private class SaveCompressedImage extends AsyncTask<String, Void, Void>{
       private ProgressDialog dialog1;

        @Override protected void onPreExecute() {
            super.onPreExecute();
            dialog1 = new ProgressDialog(context);
            dialog1.setCancelable(false);
            dialog1.setMessage("Saving");
            dialog1.show();
        }

        @Override protected Void doInBackground(String... strings) {
            if(strings[0].equals("Size")){
                try {
                    new Compressor(getApplicationContext())
                            .setQuality(percentagecompress)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(FileUtilsCompress.createFolders().getPath())
                            .compressToFile(new File(saveFilePath));
                } catch (IOException e) {
                    e.printStackTrace();}
            }else if(strings[0].equals("Resolution")){
                try {
                    new Compressor(getApplicationContext())
                            .setMaxWidth(cwidth[0])
                            .setMaxHeight(cheight[0])
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath( FileUtilsCompress.createFolders().getPath())
                            .compressToFile(new File(saveFilePath));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String name = saveFilePath.substring(saveFilePath.lastIndexOf("/") + 1);
            FileUtil.albumUpdate(context, FileUtilsCompress.createFolders().getPath() + "/" + name);
            dialog1.dismiss();
        }
    }

    //compress  image by dimensions
    private void compressDim() {
        ListCompressAdapter lviewAdapter;
        ArrayList<String> compress_option= new ArrayList<String>();
        MediaDetailsMap<String,String> mediaDetailsMap = SingleMediaActivity.mediacompress.getMainDetails(this);
        //gives in the form like 1632x1224 (2.0 MP) , getting width and height of it
        String dim[]=mediaDetailsMap.get("Resolution").split("x");
        int  width= Integer.parseInt(dim[0].replaceAll(" ",""));
        String ht[]=dim[1].split(" ");
        int height= Integer.parseInt(ht[0]);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_compresspixel, null);
        TextView title = (TextView) dialogLayout.findViewById(R.id.compress_title);
        title.setBackgroundColor(getPrimaryColor());
        //create options of compress in dimensions in multiple of 2
        int awidth=width;
        int aheight=height;
        ListView listView = (ListView)dialogLayout.findViewById(R.id.listview);
        while ((width%2==0)&&(height%2==0)) {
            compress_option.add(width + " X " + height);
            width=width/2;
            height=height/2;
        }

        lviewAdapter = new ListCompressAdapter(this, compress_option);
        listView.setAdapter(lviewAdapter);
        final int finalWidth = awidth;
        final int finalHeight = aheight;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    cwidth[0] = finalWidth ;
                    cheight[0] = finalHeight;}
                else{
                    cwidth[0] = finalWidth /(position*2);
                    cheight[0] = finalHeight /(position*2);}
                view.setBackgroundColor(R.color.md_light_blue_A400);
                new SaveCompressedImage().execute("Resolution");
                finish();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(dialogLayout);
        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface
                .BUTTON_NEGATIVE}, getAccentColor(), dialog);
    }
}
