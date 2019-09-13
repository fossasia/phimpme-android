package org.fossasia.phimpme.editor;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import id.zelory.compressor.Compressor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.editor.adapter.ListCompressAdapter;
import org.fossasia.phimpme.editor.utils.FileUtil;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.base.MediaDetailsMap;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;

public class CompressImageActivity extends ThemedActivity {

  public String saveFilePath;
  public static final String EXTRA_OUTPUT = "extra_output";
  public int percentagecompress = 0;
  public final int[] cwidth = new int[1];
  public final int[] cheight = new int[1];
  private boolean compressSize = false;
  private boolean compressDim = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      compressSize = savedInstanceState.getBoolean("CompressSize", false);
      compressDim = savedInstanceState.getBoolean("CompressDimension", false);
    }
    setContentView(R.layout.activity_compress_image);
    initView();
    Button size = findViewById(R.id.size);
    size.setBackgroundColor(getAccentColor());
    Button dimension = findViewById(R.id.bypixel);
    dimension.setBackgroundColor(getAccentColor());
    ImageButton cancel = findViewById(R.id.edit_cancel);
    size.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            compressSize();
          }
        });
    dimension.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            compressDim();
          }
        });
    cancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            finish();
          }
        });
    if (compressDim) {
      compressDim();
    } else if (compressSize) {
      compressSize();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean("CompressSize", compressSize);
    savedInstanceState.putBoolean("CompressDimension", compressDim);
  }

  private void initView() {

    ImageViewTouch imageViewToucher = findViewById(R.id.main_image);
    saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
    Uri uri = Uri.fromFile(new File(saveFilePath));
    Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageViewToucher);
    imageViewToucher.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
  }

  private void compressSize() {

    compressSize = true;
    LayoutInflater inflater = getLayoutInflater();
    View dialogLayout = inflater.inflate(R.layout.dialog_compresssize, null);
    TextView title = dialogLayout.findViewById(R.id.compress_title);
    title.setBackgroundColor(getPrimaryColor());
    SeekBar percentsize = dialogLayout.findViewById(R.id.seekBar);
    percentsize.getThumb().setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN);
    percentsize.setProgress(0);
    final TextView percent = dialogLayout.findViewById(R.id.textview2);
    percentsize.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // options of compress by size from 5% to 9;
            int progress1 = 95 - progress;
            progress1 = progress1 - progress1 % 5;
            percent.setText(progress1 + "%");
            percentagecompress = progress1;
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
          }
        });

    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    // this is set the view from XML inside AlertDialog
    alert.setView(dialogLayout);
    alert.setNegativeButton(
        getResources().getString(R.string.cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            compressSize = false;
            dialog.cancel();
          }
        });
    alert.setPositiveButton(
        getResources().getString(R.string.set),
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            new SaveCompressedImage().execute(getString(R.string.size));
            finish();
          }
        });
    AlertDialog dialog = alert.create();
    dialog.show();
  }

  private class SaveCompressedImage extends AsyncTask<String, Void, Void> {
    private ProgressDialog dialog1;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog1 = new ProgressDialog(context);
      dialog1.setCancelable(false);
      dialog1.setMessage(getString(R.string.saving));
      dialog1.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
      if (strings[0].equals(getString(R.string.size))) {
        String path = null;
        if (checkCompressFolder(saveFilePath)) {
          Bitmap bitmap = getBitmap(saveFilePath);
          path = checkforanao(bitmap);
          saveFilePath = path;
        }
        try {
          new Compressor(getApplicationContext())
              .setQuality(percentagecompress)
              .setCompressFormat(Bitmap.CompressFormat.JPEG)
              .setDestinationDirectoryPath(FileUtilsCompress.createFolders().getPath())
              .compressToFile(new File(saveFilePath));
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (strings[0].equals(getString(R.string.resolution))) {
        try {
          new Compressor(getApplicationContext())
              .setMaxWidth(cwidth[0])
              .setMaxHeight(cheight[0])
              .setCompressFormat(Bitmap.CompressFormat.JPEG)
              .setDestinationDirectoryPath(FileUtilsCompress.createFolders().getPath())
              .compressToFile(new File(saveFilePath));

        } catch (IOException e) {
          e.printStackTrace();
        }
        File file = new File(saveFilePath);
        if (file.exists()) {
          file.delete();
        }
        finish();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      String name = saveFilePath.substring(saveFilePath.lastIndexOf("/") + 1);
      FileUtil.albumUpdate(context, FileUtilsCompress.createFolders().getPath() + "/" + name);
      dialog1.dismiss();
      Toast.makeText(context, R.string.compress, Toast.LENGTH_SHORT).show();
    }
  }

  private String checkforanao(Bitmap bitmap) {
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + getString(R.string.saved_image));
    myDir.mkdirs();
    Random generator = new Random();
    int n = 10000;
    n = generator.nextInt(n);
    String fname = "Image-" + n + ".jpg";
    File file = new File(myDir, fname);
    if (file.exists()) file.delete();
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
      out.flush();
      out.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return file.getPath();
  }

  public Bitmap getBitmap(String path) {

    Uri uri = Uri.fromFile(new File(path));
    InputStream in = null;
    try {
      final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
      in = getContentResolver().openInputStream(uri);

      // Decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(in, null, o);
      in.close();

      int scale = 1;
      while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
        scale++;
      }

      Bitmap bitmap = null;
      in = getContentResolver().openInputStream(uri);
      if (scale > 1) {
        scale--;
        // scale to max possible inSampleSize that still yields an image
        // larger than target
        o = new BitmapFactory.Options();
        o.inSampleSize = scale;
        bitmap = BitmapFactory.decodeStream(in, null, o);

        // resize to desired dimensions
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
        double x = (y / height) * width;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x, (int) y, true);
        bitmap.recycle();
        bitmap = scaledBitmap;

        System.gc();
      } else {
        bitmap = BitmapFactory.decodeStream(in);
      }
      in.close();

      return bitmap;
    } catch (IOException e) {
      return null;
    }
  }

  private boolean checkCompressFolder(String path) {
    boolean result = false;
    File file = new File(FileUtilsCompress.createFolders().getPath());
    for (int i = 0; i < file.listFiles().length; i++) {
      if (file.listFiles()[i].getPath().equals(path)) {
        result = true;
        break;
      }
    }
    return result;
  }

  // compress  image by dimensions
  private void compressDim() {

    compressDim = true;
    ListCompressAdapter lviewAdapter;
    ArrayList<String> compress_option = new ArrayList<String>();
    MediaDetailsMap<String, String> mediaDetailsMap =
        SingleMediaActivity.mediacompress.getMainDetails(this);
    // gives in the form like 1632x1224 (2.0 MP) , getting width and height of it
    String dim[] = mediaDetailsMap.get(getString(R.string.resolution)).split("x");
    int width = Integer.parseInt(dim[0].replaceAll(" ", ""));
    String ht[] = dim[1].split(" ");
    int height = Integer.parseInt(ht[0]);
    LayoutInflater inflater = getLayoutInflater();
    final View dialogLayout = inflater.inflate(R.layout.dialog_compresspixel, null);
    TextView title = dialogLayout.findViewById(R.id.compress_title);
    title.setBackgroundColor(getPrimaryColor());
    // create options of compress in dimensions in multiple of 2
    int awidth = width;
    int aheight = height;
    ListView listView = dialogLayout.findViewById(R.id.listview);
    while ((width % 2 == 0) && (height % 2 == 0)) {
      compress_option.add(width + " X " + height);
      width = width / 2;
      height = height / 2;
    }

    lviewAdapter = new ListCompressAdapter(this, compress_option);
    listView.setAdapter(lviewAdapter);
    final int finalWidth = awidth;
    final int finalHeight = aheight;
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
              cwidth[0] = finalWidth;
              cheight[0] = finalHeight;
            } else {
              cwidth[0] = finalWidth / (position * 2);
              cheight[0] = finalHeight / (position * 2);
            }
            view.setBackgroundColor(R.color.md_light_blue_A400);
            new SaveCompressedImage().execute(getString(R.string.resolution));
            finish();
          }
        });

    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setView(dialogLayout);
    alert.setNegativeButton(
        getResources().getString(R.string.cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            compressDim = false;
            dialog.cancel();
          }
        });
    AlertDialog dialog = alert.create();
    dialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        dialog);
  }
}
